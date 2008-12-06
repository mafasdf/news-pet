$(function(){
    $('.dropdown').hide();
    $('.menu').click(function(){
        $('.dropdown')
            .css('position','absolute')
            .css('top',$(this).position().top + $(this).outerHeight())
            .css('left',$(this).position().left)
            .toggle("fast")
    });
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
    $(".star").hover(turn_gold, turn_blue)
})
function turn_gold(e){
    $(this).attr('src', '{{MEDIA_URL}}img/gold_star.png')
}
function turn_blue(e){
    $(this).attr('src', '{{MEDIA_URL}}img/star.png')
}