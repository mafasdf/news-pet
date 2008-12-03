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
    if request.user.is_authenticated():
        id = Category.objects.filter(owner = request.user)[0].id
        return HttpResponseRedirect(reverse('f_category', args=[id]))
    else:
        return django.contrib.auth.views.login(template_name = 'feed/home.html')

@login_required
def manage_categories(request, category_id=None):
    category = None
    if category_id is not None:
        category = get_object_or_404(Category, id=category_id)
    category_form = CategoryForm(request.POST or None, instance = category)
    if category_form.is_valid():
        category = category_form.save(commit=False)
        category.owner = request.user
        category.save()
        training.train_category(category)
        return HttpResponseRedirect(reverse('f_manage_categories'))
    categories = request.user.category_set.all()
    context = {'category_form': category_form, 'categories': categories}
    return render_to_response('feed/manage_categories.html',context, context_instance = RequestContext(request))

@login_required
def manage_feeds(request):
    return render_to_response('base.html',{}, context_instance = RequestContext(request))
    
@login_required
def delete_category(request, category_id):
    return delete_object(request,
                         model = Category,
                         object_id = category_id,
                         post_delete_redirect = reverse('f_manage_categories'),
                         template_name = "feed/delete.html",
                         login_required = login_required
                        )

def opinionate(request, item_id, opinion):
    item = get_object_or_404(FeedItem, id = item_id)
    training.train_item(item, item.category, opinion)
    item.opinion = opinion
    item.save()
    return HttpResponseRedirect(reverse('f_item', args=[item.id]))

def move(request, item_id, new_category):
    item = get_object_or_404(FeedItem, id = item_id)
    category = get_object_or_404(FeedItem, id = item_id)
    training.train_item(item, category, -1)
    training.train_item(item, item.category, 1)
    item.category = category
    item.opinion = 1
    item.save()
    return HttpResponseRedirect(reverse('f_item', args=[item.id]))