module namespace _= "argon/tree";

import module namespace config = "argon/config" at "config.xqm";

declare
  %rest:GET
  %rest:path("/argon.html")
  %rest:query-param("url", "{$OXY-URL}", '')
  %rest:query-param("author", "{$AUTHOR}", '')
  %output:method("xhtml")
  %output:html-version("5.0")
function _:argon($OXY-URL as xs:string, $AUTHOR as xs:string) as item() {
<html>
    <head>
        <script src="http://{$config:HOST}:8282/oxygen-xml-web-author/app/{$config:OXY-VER}-bower_components/jquery/jquery.min.js"></script>
        <link href="/static/js/fancytree/skin-win8/ui.fancytree.min.css" rel="stylesheet"/>
        <link href="/static/tree.css" rel="stylesheet"/>
        <script src="/static/js/fancytree/jquery.fancytree-all-deps.min.js"></script>
        <script src="/static/js/tree.js"></script>
    </head>
    <body>
        <iframe src="http://{$config:HOST}:8282/oxygen-xml-web-author/app/oxygen.html{
            if (empty($OXY-URL) and empty($AUTHOR)) then '' else (
                '?' || ( if (empty($OXY-URL)) then ('author=' || $AUTHOR) else (('url=' || $OXY-URL) || (if (empty($AUTHOR)) then '' else ('&amp;' || 'author=' || $AUTHOR) ) ) )
            )
        }"
            width="85%" onload="this.height=window.innerHeight;" align="right" id="oxygenFrame" name="oxygen" title="oXygen Web Author"></iframe>
        <div>
            <div>
                <h2 style="display: inline-block">Argon Connector</h2>
            </div>
            <div>
                <div id="argon-tree" style="overflow: scroll"></div>
            </div>
        </div>
    </body>
</html>
};