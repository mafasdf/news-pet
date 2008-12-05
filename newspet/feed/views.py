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
        return login(request, template_name = 'feed/home.html')

@login_required
def manage_categories(request, category_id=None):
    category = None
    training_set_form = None
    if category_id is not None:
        category = get_object_or_404(Category, id=category_id, owner=request.user)
    else:
        training_set_form = TrainingSetForm(request.POST or None)
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
    get_object_or_404(Category, id=category_id, owner=request.user)
    return delete_object(request,
                         model = Category,
                         object_id = category_id,
                         post_delete_redirect = reverse('f_manage_categories'),
                         template_name = "feed/delete.html",
                         login_required = login_required
                        )
                        
@login_required
def love_item(request, item_id):
    return opinionate(request, item_id, training.GOOD_OPINION)

@login_required
def hate_item(request, item_id):
    return opinionate(request, item_id, training.BAD_OPINION)
    
@login_required
def opinionate(request, item_id, opinion):
    item = get_object_or_404(FeedItem, id = item_id, category__owner=request.user)
    trash = Category.objects.get_trash(request.user)
    training.train_item(item, item.category, opinion, trash)
    return HttpResponseRedirect(reverse('f_item', args=[item.id]))

@login_required
def move(request, item_id, new_category):
    item = get_object_or_404(FeedItem, id = item_id, category__owner=request.user)
    category = get_object_or_404(FeedItem, id = item_id, owner=request.user)
    training.train_item(item, category)
    item.category = category
    item.save()
    return HttpResponseRedirect(reverse('f_item', args=[item.id]))