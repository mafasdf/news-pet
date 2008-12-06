$(function(){
    $('.dropdown').hide();
    $('.menu').click(function(){
        $('.dropdown')
            .css('position','absolute')
            .css('top',$(this).position().top + $(this).outerHeight())
            .css('left',$(this).position().left)
            .toggle("fast")
    });
    $(".star").hover(turn_gold, turn_blue)
    $('.category-selct select')
    .change(function(){
        $(this).parent().get(1).submit()
    })
});
// $(function(){
//     $('.list-element .post-body').hide()
//     $('.list-element .post-header')
//         .click(function(){
//             $(this)
//                 .next()
//                     .toggle('fast');
//             
//         });
// });
function change_category(id){
    $('#form-category-'+id).toggle();
    $('#category-'+id).toggle();
    $('#change-'+id).text('Cancel').attr('href','javascript:cancel_change_category("'+id+'")');
}
function cancel_change_category(id, name){
    $('#form-category-'+id).toggle();
    $('#category-'+id).toggle();
    $('#change-'+id).text('Move Item').attr('href','javascript:change_category("'+id+'")');
}
$(function() {

})
function turn_gold(e){
    $(this).attr('src', '{{MEDIA_URL}}img/gold_star.png')
}
function turn_blue(e){
    $(this).attr('src', '{{MEDIA_URL}}img/star.png')
}