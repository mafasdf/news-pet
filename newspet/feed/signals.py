from django.db.models.signals import post_save
from django.contrib.auth.models import User
from feed.models import Category

def create_trash(sender, instance = None, **kwargs):
    if instance is not None:
        Category.objects.create_trash(instance)
        

post_save.connect(create_trash, sender=User)
