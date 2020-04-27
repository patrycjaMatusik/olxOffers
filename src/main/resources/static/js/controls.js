function submitKeywordSearch() {
  $('#searchForKeywordForm').submit(function (event) {
    event.preventDefault();
    var keyword = $("#keyword").val();
    $.ajax({
      url : "/offers/olx/" + keyword,
      type: "get"
    }).done(function(response){
      $('#olxOffers').val(prettifyJson(response))
    });

  })
}

function prettifyJson(jsonObject){
  return JSON.stringify(jsonObject, undefined, 4);
}
