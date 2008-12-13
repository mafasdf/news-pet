from models import Post
from django.shortcuts import render_to_response, get_object_or_404
from django.template import RequestContext

def post_list(request):
    """docstring for post_list"""
    posts = Post.objects.all()
    context = {'posts': posts}
    return render_to_response('blog/blog.html', context, context_instance = RequestContext(request))
    
def post_detail(request, slug):
    post = get_object_or_404(Post, slug = slug)
    context = {'post': post}
    return render_to_response('blog/blog_detail.html', context, context_instance = RequestContext(request))