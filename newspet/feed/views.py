from django.shortcuts import render_to_response, get_object_or_404
from django.views.generic.create_update import delete_object
from django.http import HttpResponseRedirect, Http404, HttpResponse
from django.template import RequestContext
from django.core.urlresolvers import reverse
from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required
from django.views.generic.create_update import delete_object
from django.conf import settings
from models import *

@login_required
def my_categories(request):
    categories = Category.objects.filter(owner = request.user)
    context = {'categories': category}
    return render_to_response('feed/my_categories.html', context, context_instance = RequestContext(request))

@login_required
def category(request, category_id):
    category = get_object_or_404(Category, id = category_id, owner = request.user)
    context = {'category': category}
    return render_to_response('feed/category.html', context, context_instance = RequestContext(request))

@login_required
def item(request, item_id):
    item = get_object_or_404(FeedItem, id=item_id, feed__subscriber=request.user)
    if not item.was_viewed:
        item.was_viewed = True
        item.save()
    context = {'item': item}
    return render_to_response('feed/item.html', context, context_instance = RequestContext(request))

def home(request):
    return render_to_response('base.html',{}, context_instance = RequestContext(request)))
    
def manage_categories(request, item_id):
    return render_to_response('base.html',{}, context_instance = RequestContext(request)))

def manage_feeds(request, item_id):
    return render_to_response('base.html',{}, context_instance = RequestContext(request)))