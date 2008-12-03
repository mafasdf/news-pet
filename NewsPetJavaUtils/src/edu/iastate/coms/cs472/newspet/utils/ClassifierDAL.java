package edu.iastate.coms.cs472.newspet.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import cc.mallet.classify.NaiveBayes;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.pipe.Pipe;

public class ClassifierDAL
{
	//the 
	private static final String LOCK_PREFIX = "classifierUpdate_";
	private static final String TABLE_NAME = "Classifier";
	private static final String CLASSIFIERGROUP_COLUMN = "serializedClassifier";
	private static final String ID_COLUMN = "ID";
	
	/**
	 * Returns a group of objects consisting of a ClassifierTrainer, an instance
	 * Pipe, and a Connection object.
	 * <em>Since the Connection object is associated with a row-lock: In order to
	 * allow other threads to update the same classifier, each call to this
	 * method must be followed by a call to
	 * {@link ClassifierDAL#updateTrainerAndClassifier(ClassifierDAL.TrainerCheckoutData)}
	 * when processing the update is complete.</em>
	 * 
	 * @param classifierId
	 *        the ID of the classifier row to load the trainer for.
	 * @return
	 */
	public static TrainerCheckoutData getTrainerForUpdating(long classifierId)
	{
		Connection conn = createConnection();
		
		try
		{
			//acquire lock on string based on ID
			PreparedStatement getLock = conn.prepareStatement("SELECT GET_LOCK(?, ?) ;");
			getLock.setString(1, LOCK_PREFIX + classifierId);
			getLock.setInt(2, Integer.MAX_VALUE);//TODO set different timeout value?
			getLock.execute();
			getLock.close();
			
			//get existing
			PreparedStatement getGroup = conn.prepareStatement("SELECT " + CLASSIFIERGROUP_COLUMN + " FROM " + TABLE_NAME + " WHERE " + ID_COLUMN
					+ "=?;");
			getGroup.setLong(1, classifierId);
			ResultSet results = getGroup.executeQuery();
			
			NaiveBayesTrainer trainer;
			Pipe pipe;
			
			//New trainer: we need to create one.
			if(!results.next())
			{
				PreparedStatement insertNew = conn.prepareStatement("INSERT INTO " + TABLE_NAME + " (ID) values (?)");
				insertNew.setLong(1, classifierId);
				insertNew.executeUpdate();
				insertNew.close();
				trainer = new NaiveBayesTrainer();
				pipe = DocumentConversion.createConversionPipes();
			}
			else
			{
				//deserialize
				Blob blob = results.getBlob(CLASSIFIERGROUP_COLUMN);
				if(blob != null)
				{
					ClassifierObjectGroup group = (ClassifierObjectGroup) getObjectFromStream(blob.getBinaryStream());
					trainer = group.getTrainer();
					pipe = group.getPipe();
				}
				else
				//the last execution probably failed
				{
					trainer = new NaiveBayesTrainer();
					pipe = DocumentConversion.createConversionPipes();
				}
			}
			
			getGroup.close();
			
			return new TrainerCheckoutData(trainer, conn, pipe, classifierId);
			
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Couldn't get trainer data for ID " + classifierId, e);
		}
	}
	
	/**
	 * Persists an updated trainer and matching classifier.
	 * 
	 * @param checkin
	 */
	public static void updateTrainerAndClassifier(TrainerCheckoutData checkin)
	{
		try
		{
			Object toPersist = new ClassifierObjectGroup(checkin.trainer.getClassifier(), checkin.trainer, checkin.pipe);
			
			PreparedStatement update = checkin.connection.prepareStatement("UPDATE " + TABLE_NAME + " SET " + CLASSIFIERGROUP_COLUMN + "=? WHERE "
					+ ID_COLUMN + "=?");
			update.setBlob(1, getInputStreamFromObject(toPersist));
			update.setLong(2, checkin.classifierID);
			update.executeUpdate();
			update.close();
			
			
			//unlock
			PreparedStatement releaseLock = checkin.connection.prepareStatement("SELECT RELEASE_LOCK(?) ;");
			releaseLock.setString(1, LOCK_PREFIX + checkin.classifierID);
			releaseLock.execute();
			releaseLock.close();
			
			//TODO: java.sql.SQLException: Can't call commit when autocommit=true
			// checkin.connection.commit();
			checkin.connection.close();
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not persist trainer/classifier for ID " + checkin.classifierID, e);
		}
	}
	
	/**
	 * Lockless: in the case that the classifier is currently being trained, the
	 * last known good copy of it will be returned.
	 * <p>
	 * Will return null if the classifier does not exist or hahs not yet been
	 * trained for the first time.
	 * </p>
	 */
	public static NaiveBayes getClassifier(long classifierID)
	{
		Connection conn = createConnection();
		
		ClassifierObjectGroup group;
		try
		{
			PreparedStatement getGroup = conn.prepareStatement("SELECT " + CLASSIFIERGROUP_COLUMN + " FROM " + TABLE_NAME + " WHERE " + ID_COLUMN
					+ "=?;");
			getGroup.setLong(1, classifierID);
			ResultSet results = getGroup.executeQuery();
			
			//check for nonexistent record
			if(!results.next()) return null;
			
			//get binary data
			Blob blob = results.getBlob(CLASSIFIERGROUP_COLUMN);
			
			//check if first training is incomplete
			if(blob == null) return null;
			
			group = (ClassifierObjectGroup) getObjectFromStream(blob.getBinaryStream());
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not retrieve classifier for ID " + classifierID, e);
		}
		
		return group.getClassifier();
	}
	
	/**
	 * Class for returning multiple values in
	 * {@link ClassifierDAL#getTrainerForUpdating(long)}
	 * 
	 * @author Michael Fulker
	 */
	public static class TrainerCheckoutData
	{
		private NaiveBayesTrainer trainer;
		private Connection connection;
		private Pipe pipe;
		private long classifierID;
		
		public TrainerCheckoutData(NaiveBayesTrainer trainer, Connection connection, Pipe pipe, long classifierID)
		{
			this.trainer = trainer;
			this.connection = connection;
			this.pipe = pipe;
			this.classifierID = classifierID;
		}
		
		public NaiveBayesTrainer getTrainer()
		{
			return trainer;
		}
		
		public Pipe getPipe()
		{
			return pipe;
		}
		
	}
	
	private static Connection createConnection()
	{
		try
		{
			//TODO: make configurable
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			return DriverManager.getConnection("jdbc:mysql://localhost/newspet", "root", "34eszx");
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not establish connection with database", e);
		}
		catch(InstantiationException e)
		{
			throw new RuntimeException("Could not instantiate jdbc driver", e);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException("Could not instantiate jdbc driver", e);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException("Could not instantiate jdbc driver", e);
		}
	}
	
	/**
	 * Consolidated deserialization method in case we ever want to add
	 * compression/decompression.
	 */
	private static Object getObjectFromStream(InputStream in)
	{
		try
		{
			InputStream decompressor = new java.util.zip.InflaterInputStream(in);
			//ObjectInputStream objReader = new ObjectInputStream(in);
			ObjectInputStream objReader = new ObjectInputStream(decompressor);
			return objReader.readObject();
		}
		catch(IOException e)
		{
			throw new RuntimeException("Could not deserialize", e);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException("Could not deserialize", e);
		}
	}
	
	private static InputStream getInputStreamFromObject(Object o)
	{
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		try
		{
			OutputStream compressor = new java.util.zip.DeflaterOutputStream(bytesOut);
			ObjectOutputStream objWriter = new ObjectOutputStream(compressor);
			//ObjectOutputStream objWriter = new ObjectOutputStream(bytesOut);
			objWriter.writeObject(o);
			objWriter.close();
			compressor.close();
			bytesOut.close();
		}
		catch(IOException e)
		{
			throw new RuntimeException("Could not serialize", e);
		}
		
		return new ByteArrayInputStream(bytesOut.toByteArray());
	}
}
