from django.db import models
from django.contrib.auth.models import User
from django.core.urlresolvers import reverse
import datetime

class Feed(models.Model):
    url = models.URLField("Feed Url", max_length=4000, unique=True)
    title = models.CharField(max_length=255)
    description = models.TextField("Description (Optional)", null=True, blank=True)
    subscriber = models.ForeignKey(User)
    
    #denormalized data
    last_crawled = models.DateTimeField(default=datetime.datetime.min)
    
    def __unicode__(self):
        return self.title
    
    def get_absolute_url(self):
        return self.url
    
class FeedItemManager(models.Manager):
    def unread(self):
        return self.filter(was_viewed = False)

class FeedItem(models.Model):
    date_added = models.DateTimeField(default=datetime.datetime.now)
    title = models.CharField(max_length=255)
    body = models.TextField()
    opinion = models.IntegerField(default=0)
    link = models.URLField()
    category = models.ForeignKey("Category")
    feed = models.ForeignKey("Feed")
    was_viewed = models.BooleanField(default=False)
    
    objects = FeedItemManager()
    
    def get_internal_url(self):
        return reverse('f_item', args=[self.id])
    
    def get_external_url(self):
        return self.link
    
    def __unicode__(self):
        return self.title
    
class Category(models.Model):
    name = models.CharField(max_length=63)
    is_trash = models.BooleanField(default=False)
    owner = models.ForeignKey(User)
    
    def feed_items(self):
        self.feeditem_set.all()
        
    def has_unread(self):
        return self.unread_count() > 0
    
    def unread_count(self):
        return self.unread().count()
    
    def unread(self):
        return self.feeditem_set.unread()
    
    def __unicode__(self):
        return self.name
    
    def get_absolute_url(self):
        return reverse('f_category', args=[self.id])
        
class PreTrainedClassifier(models.Model):
    name = models.CharField(max_length=255)
    
    def __unidcode__(self):
        return self.name
    