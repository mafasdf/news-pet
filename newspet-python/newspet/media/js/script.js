$(function(){
    $('.dropdown').hide();
    $('.menu').click(function(){
        $('.dropdown')
            .css('position','absolute')
            .css('top',$(this).position().top + $(this).outerHeight())
            .css('left',$(this).position().left)
            .toggle("fast")
    });
    $(".star").hover(turn_gold, turn_blue);
    $('.category-select select')
    .change(function(){
        $(this).parent().get(0).submit();
    })
    $(".collapser").toggle(uncollapse, collapse);
    $(".subdirectory").hide();
});

function uncollapse(){
    $(this).attr('src', '/site-media/img/uncollapsed.png');
    $('#subdirectory-'+this.id).show('fast');
}
function collapse(){
    $(this).attr('src', '/site-media/img/collapsed.png');
    $('#subdirectory-'+this.id).hide('fast');
}
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
    $(this).attr('src', '/site-media/img/gold_star.png')
}
function turn_blue(e){
    $(this).attr('src', '/site-media/img/star.png')
}