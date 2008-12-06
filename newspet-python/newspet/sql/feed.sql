
CREATE TABLE Classifier
(
ID int NOT NULL,
serializedClassifier longblob,
CONSTRAINT PRIMARY KEY USING HASH (ID),
CONSTRAINT FOREIGN KEY (ID) REFERENCES auth_user(ID)
);
