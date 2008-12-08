from django.shortcuts import render_to_response, get_object_or_404
from django.views.generic.create_update import delete_object
from django.http import HttpResponseRedirect, Http404, HttpResponse
from django.template import RequestContext
from django.core.urlresolvers import reverse
from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required
from django.views.generic.create_update import delete_object
from django.conf import settings
from django.contrib.auth.views import login
from models import *
from forms import *
import training




@login_required
def my_categories(request):
    categories = Category.objects.filter(owner = request.user)
    context = {'categories': category}
    return render_to_response('feed/my_categories.html', context, context_instance = RequestContext(request))

@login_required
def category(request, category_id):
    category = get_object_or_404(Category, id = category_id, owner = request.user)
    context = {'category': category, 'items': category.feed_items()}
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
    if request.user.is_authenticated():
        id = Category.objects.filter(owner = request.user)[0].id
        return HttpResponseRedirect(reverse('f_category', args=[id]))
    else:
        return login(request, template_name = 'feed/home.html')

@login_required
def manage_categories(request, category_id=None):
    category = None
    training_set_form = None
    if category_id is not None:
        category = get_object_or_404(Category, id=category_id, owner=request.user, is_trash=False)
    else:
        training_set_form = TrainingSetForm(request.POST or None)
    category_form = CategoryForm(request.POST or None, instance = category)
    if category_form.is_valid() and ( not training_set_form or training_set_form.is_valid() ):
        category = category_form.save(commit=False)
        category.owner = request.user
        category.save()
        if training_set_form:
            training.train_category(category, training_set_form.get_training_set())
        return HttpResponseRedirect(reverse('f_manage_categories'))
    categories = request.user.category_set.all()
    context = {'category_form': category_form, 'categories': categories, 'training_set_form': training_set_form, 'category': category}
    return render_to_response('feed/manage_categories.html',context, context_instance = RequestContext(request))

@login_required
def manage_feeds(request, feed_id=None):
    feed = None
    if feed_id is not None:
        feed = get_object_or_404(Feed, id=feed_id, subscriber=request.user)
    feed_form = FeedForm(request.POST or None, instance = feed)
    if feed_form.is_valid():
        feed = feed_form.save(commit=False)
        feed.subscriber = request.user
        feed.save()
        return HttpResponseRedirect(reverse('f_manage_feeds'))
    feeds = request.user.feed_set.all()
    context = {'feed_form': feed_form, 'feeds': feeds, 'feed': feed}
    return render_to_response('feed/manage_feeds.html',context, context_instance = RequestContext(request))

def delete_feed(request, feed_id):
    get_object_or_404(Feed, id=feed_id, subscriber=request.user)
    return delete_object(request,
                         model = Feed,
                         object_id = feed_id,
                         post_delete_redirect = reverse('f_manage_feeds'),
                         template_name = "feed/delete.html",
                         login_required = login_required
                        )

# @login_required
# def delete_category(request, category_id):
#     get_object_or_404(Category, id=category_id, owner=request.user)
#     return delete_object(request,
#                          model = Category,
#                          object_id = category_id,
#                          post_delete_redirect = reverse('f_manage_categories'),
#                          template_name = "feed/delete.html",
#                          login_required = login_required
#                         )
                        
@login_required
def love_item(request, item_id):
    return opinionate(request, item_id, training.GOOD_OPINION)

@login_required
def hate_item(request, item_id):
    return opinionate(request, item_id, training.BAD_OPINION)
    
@login_required
def opinionate(request, item_id, opinion):
    item = get_object_or_404(FeedItem, id = item_id, category__owner=request.user)
    item.opinion = opinion
    item.save()
    training.train_item(item, item.category, opinion)
    return HttpResponseRedirect(reverse('f_item', args=[item.id]))

@login_required
def move(request, item_id):
    category_change_form = CategoryChangeForm(request.user, None, data=request.POST)
    if category_change_form.is_valid():
        item = get_object_or_404(FeedItem, id = item_id, category__owner=request.user)
        category = item.category
        new_category = category_change_form.new_category
        training.train_item(item, category, training.BAD_OPINION)
        training.train_item(item, new_category, training.GOOD_OPINION)
        item.category = new_category
        item.opinion = training.GOOD_OPINION
        item.save()
    else:
        print category_change_form.errors
    return HttpResponseRedirect(reverse('f_item', args=[item_id]))

UP_DIRECTION = 'up'
DOWN_DIRECTION = 'down'
#TODO add normalization incase of improper ordering
def category_position_change(request, category_id, direction):
    category = get_object_or_404(Category, id = category_id)
    position = category.position
    categories = Category.objects.filter(owner = request.user).order_by('position')
    if direction is UP_DIRECTION:
        new_position = position - 1
    else:
        new_position = position + 1
    if new_position >= 0 and new_position < len(categories):
        categories[new_position].position = position
        categories[new_position].save()
        category.position = new_position
        category.save()
    return HttpResponseRedirect(reverse('f_manage_categories'))

        
    
    