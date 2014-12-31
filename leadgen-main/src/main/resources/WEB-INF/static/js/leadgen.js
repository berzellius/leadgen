var initLeadgen = (function(){
    return function(){
        var $ = (typeof(jQuery) != 'udefined')? jQuery : false;
        var urlParams;


        (window.onpopstate = function () {
            urlParams = leadgenSearchEngine.urlStringToParams(window.location.search.substring(1));
        })();

       if($('form#filters').length > 0){
           // load leadgen search engine
           leadgenSearchEngine.init(
               urlParams,
               $('form#filters'),
               $('div.pagination').length > 0 ?
                   $('div.pagination') : null
           );
       }


       $('button#btn_add_file').click(function(){

           var divGroup = $(this).parents("div").eq(0);

           inpFile = document.createElement("input");
           $(inpFile).attr('type','file');
           $(inpFile).attr('name', 'attachFile[]');


           inpName = document.createElement("input");
           $(inpName).attr('type', 'text');
           $(inpName).attr('name','fileName[]');
           $(inpName).attr('placeholder', 'Имя файла');

           div = document.createElement("div");
           $(div).addClass("form-group");

           $(div).append(inpFile).append(inpName);
           divGroup.after(div);

           return false;
       });

    }
})();

jQuery(document).ready(function($){
    initLeadgen($);
});

var leadgenSearchEngine = {

    urlParams : null,
    form: null,
    paginator : null,

    init : function(up, f, p){
        this.urlParams = up;
        this.paginator = p;

        this.bindActions();
    },

    bindActions : function(){
        if(this.paginator != null){

            $('a', this.paginator).click(
                (function(t){
                    return function(){
                        var search = t.getSearchPartFromUrl(this.href);

                        if(!search) redirect(this.href);

                        var params = t.urlStringToParams(search);

                        for(p in t.urlParams){
                            if(p != 'page' && p != 'size')
                                params[p] = t.urlParams[p];
                        }

                        var urlToRedirect = t.getUrl(
                            t.getAddressFromUrl(this.href),
                            params
                        );

                        t.redirect(urlToRedirect);

                        return false;
                    }
                })(this)
            );
        }
    },

    getSearchPartFromUrl : function(url){
        var pt = url.split('?');
        if(pt.length > 1) return pt[1];
        else return false;
    },

    getAddressFromUrl : function(url){
        var pt = url.split('?');
        if(pt.length > 1) return pt[0];
        else return url;
    },

    urlStringToParams : function(string){
        var match,
            pl     = /\+/g,  // Regex for replacing addition symbol with a space
            search = /([^&=]+)=?([^&]*)/g,
            decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
            query  = string;

        urlParams = {};
        while (match = search.exec(query))
            urlParams[decode(match[1])] = decode(match[2]);

        return urlParams;
    },

    redirect : function(url){
        window.location.href = url;
    },

    getUrl : function getURL(theUrl, extraParameters) {
        var extraParametersEncoded = $.param(extraParameters);
        var seperator = theUrl.indexOf('?') == -1 ? "?" : "&";

        return(theUrl + seperator + extraParametersEncoded);
    }
}