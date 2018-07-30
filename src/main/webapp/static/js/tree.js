$(function(){  // on page load
   $.ajax("/check-login",
   {
     //type: "HEAD",
     statusCode: {
     403: function() {
        //console.log("401");
        window.location = "/login?path=argon.html";
     }
   }}).done(function(msg){console.log("OK");});
  
  $("#argon-tree").fancytree({
    source: [ {title: "BaseX Server", key: "databases", folder: true, lazy: true} ],
    lazyLoad: function(event, data) {
        var node = data.node;
        data.result = {
            url: "/folders",
            data: {url: node.key + '/', tree: true}
        };
    },
    activate: function(event, data){
        var node = data.node;
        if (!node.folder) {
            var fileLink = encodeURIComponent("argon://" + node.key);
            var author = new URL(location.href).searchParams.get('author');
            document.getElementById("oxygenFrame").src="http://" + location.hostname + ":8282/oxygen-xml-web-author/app/oxygen.html?url=" + fileLink +
                (author ? "&author=" + author : "");
        }
    }
  });
});