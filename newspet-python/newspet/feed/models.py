from django.db import models
from django.contrib.auth.models import User
from django.core.urlresolvers import reverse
from django.utils.safestring import mark_safe
import datetime
import training

class Feed(models.Model):
    url = models.URLField("Feed Url", max_length=4000, unique=True)
    title = models.CharField(max_length=255)
    description = models.TextField("Description (Optional)", null=True, blank=True)
    subscriber = models.ForeignKey(User)
    
    #denormalized data
    last_crawled = models.DateTimeField(default=datetime.datetime(2000,1,1))
    
    def __unicode__(self):
        return self.title
    
    def get_absolute_url(self):
        return self.url
    
class FeedItemManager(models.Manager):
    def unread(self):
        return self.filter(was_viewed = False)

class FeedItem(models.Model):
# TODO    date_added = models.DateTimeField()
    title = models.CharField(max_length=255)
    author = models.CharField(max_length=255)
    body = models.TextField()
    link = models.URLField()
    opinion = models.IntegerField(default=0)
    category = models.ForeignKey("Category")
    feed = models.ForeignKey("Feed")
    was_viewed = models.BooleanField(default=False)
    
    objects = FeedItemManager()
    
    def get_internal_url(self):
        return reverse('f_item', args=[self.id])
    
    def get_external_url(self):
        return self.link
    
    def get_body(self): 
        return mark_safe(self.body)
    
    def is_liked(self):
        if self.opinion is training.GOOD_OPINION:
            return True
        return False
        
    safe_body = property(get_body)
    
    def __unicode__(self):
        return self.title
    # TODO class Meta:
    #     ordering = ['-date_added']
    
class CategoryManager(models.Manager):
    def get_trash(self, user):
        return self.filter(owner=user, is_trash=True)[0]
    
    def create_trash(self, user):
        try:
            Category.objects.get(is_trash=True, owner=user)
        except Category.DoesNotExist:
            trash = Category(name="Trash", is_trash=True, owner=user)
            trash.save()
    
class Category(models.Model):
    name = models.CharField(max_length=25)
    is_trash = models.BooleanField(default=False)
    owner = models.ForeignKey(User)
    position = models.IntegerField()
    
    objects = CategoryManager()
    
    def feed_items(self):
        return self.feeditem_set.all()
        
    def has_unread(self):
        return self.unread_count() > 0
    
    def sorted_items(self):
        return self.feeditem_set.order_by('was_viewed')[:5]
    
    def unread_count(self):
        return self.unread().count()
    
    def unread(self):
        return self.feeditem_set.unread()
    
    def __unicode__(self):
        return self.name
    
    def get_absolute_url(self):
        return reverse('f_category', args=[self.id])

    def save(self):
        if self.id is None:
            self.position = len(Category.objects.filter(owner = self.owner))
        super(Category,self).save()
        
        
    class Meta:
        ordering = ['position']
        
class TrainingSet(models.Model):
    name = models.CharField(max_length=255)
    path = models.TextField()
    
    def __unicode__(self):
        return self.name
    