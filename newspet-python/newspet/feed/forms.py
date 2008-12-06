from django import forms
from models import *

class CategoryForm(forms.ModelForm):
    name = forms.CharField(max_length = 63)
    class Meta:
        model = Category
        exclude = ('is_trash', 'owner')

TRAINING_CHOICES = (('Pre-Trained Categories', 0),
                    ('Word or Phrase', 1),
                    ('Load Batch Files', 2),)

class TrainingSetChoiceForm(forms.Form):
    method = forms.ChoiceField( choices= TRAINING_CHOICES,
                                widget = forms.RadioSelect())

class TrainingSetForm(forms.Form):
    def __init__(self, *args, **kwargs):
        """docstring for __init__"""
        super(TrainingSetForm, self).__init__(*args, **kwargs)
        self.fields['training_set'].choices = [(ts.id, ts) for ts in TrainingSet.objects.all()]
    
    training_set = forms.ChoiceField(label="Category for Initial Categorization")
    
    def clean(self):
        super(TrainingSetForm, self).clean
        if 'training_set' in self.cleaned_data:
            training_set_id = self.cleaned_data['training_set']
        else:
            raise forms.ValidationError("You must choose initial training information")
        try:
            self.training_set = TrainingSet.objects.get(id = training_set_id)
        except TrainingSet.DoesNotExist:
            self.training_set = None
            raise forms.ValidationError("The initial training you have choosen does not seem to exist. Please pick again.")
    
    def get_training_set(self):
        return self.training_set

class CategoryChangeForm(forms.Form):
    def __init__(self, user, *args, **kwargs):
        """docstring for __init__"""
        super(CategoryChangeForm, self).__init__(*args, **kwargs)
        # self.fields['new_category'].choices = [(c.id, c) for c in Category.objects.filter(owner = user)]
        self.fields['category'].queryset = Category.objects.filter(owner = user)
        
    category = forms.ModelChoiceField(Category.objects.none())
    
    def get_new_category(self):
        return self.cleaned_data['category']
    new_category = property(get_new_category)
class PhraseForm(forms.Form):
    phrase = forms.CharField(max_length = 127)

class BatchFileForm(forms.Form):
    files = forms.CharField(max_length = 500)

class FeedForm(forms.ModelForm):
    class Meta:
        model = Feed
        exclude = ('subscriber', 'last_crawled')
    