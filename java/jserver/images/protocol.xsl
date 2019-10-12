<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="2.0"
  >
  <xsl:output method="html"/> 
  <xsl:strip-space elements="SECT"/>
 
  <xsl:template match="tests">
    <HTML><body>
     <h1>Trainer Protocol</h1>
     <h2>Statistic</h2>
     <table>
     <tr><td>Patterns</td> <td> <xsl:value-of select="count(test)"/> </td></tr>
     <tr><td>Fails</td>    <td> <xsl:value-of select="count(test/fail)"/> </td></tr>
     <tr><td>Hits</td>     <td> <xsl:value-of select="count(test/hit)"/> </td></tr>
 
     </table>
       <xsl:apply-templates select="test" />
    </body></HTML>
  </xsl:template>

  <xsl:template match="analyses">
    <HTML>
     <head>
     <style>
	body {background-color: powderblue;}
	h1   {color: blue;}
        pre  {background-color: yellow; display: none;}
        .warning {color: red; }
	.showHide {
		padding:10px;
		background: rgb(254, 164, 0);
		box-shadow:none!important;
		color:#fff;?font-weight:bold;
	}
	</style>
	<script> 
	function show(id) { 
    		if(document.getElementById) { 
        		var mydiv = document.getElementById(id); 
        		mydiv.style.display = (mydiv.style.display=='block'?'none':'block'); 
    		} 
	}  
	</script>      
     </head>
     <body>
     <h1>Analyser Protocol V1.0</h1>
     <div>Datum: <xsl:value-of select="date" /> </div>
     <div>Anzahl Dateien: <xsl:value-of select="count(file)" /> </div>
     <div>Anzahl Snippets: <xsl:value-of select="count(file/analysis)" /> </div>
     <xsl:apply-templates select="file" />
     
     </body></HTML>
  </xsl:template>

  <xsl:template match="file">
  <h2>**** <xsl:value-of select="directory" /> - <xsl:value-of select="fileName" /> ************ </h2>
     <xsl:choose>
     <xsl:when test="NumSnippets = 0">
	<div  class="warning">NoSnippets: keine passenden Snippets gefunden! </div>
     </xsl:when>
     <xsl:otherwise>
      <div>gefundene Snippets: <xsl:value-of select="NumSnippets" /> </div>
     </xsl:otherwise>
   </xsl:choose>
   <xsl:for-each select="Warning">
  	<div  class="warning"> Warnung: <xsl:value-of select="text()" /> </div>
   </xsl:for-each> 
     <xsl:apply-templates select="analysis" />
  </xsl:template>


  <xsl:template match="analysis">
  <h3>************** Snippet <xsl:value-of select="snippet-name" /> *************</h3>
  <a href="" class="showHide">
	<xsl:attribute name="onclick">javascript:show('<xsl:value-of select="snippet-name" />'); return false</xsl:attribute> 
	Code Ein- / Ausblenden</a> 
  <pre><xsl:attribute name="id"><xsl:value-of select="snippet-name" /></xsl:attribute>
	<xsl:value-of select="code" />
  </pre>
   <div><img><xsl:attribute name="src"> 
           <xsl:value-of select="imageFile" /> </xsl:attribute> 
           <xsl:attribute name="width">200</xsl:attribute> 
      </img></div>
  <div>Anzahl der Symbole: <xsl:value-of select="symbols" /></div>
  <div>Bytes: <xsl:value-of select="size" /> zip-Bytes: <xsl:value-of select="zip-size" /></div>
  <div>Gesendete BoSL-Befehle: <xsl:value-of select="BoSL-commands" /></div>
  <div>Notwendige BoSL-Befehle: <xsl:value-of select="required-bosl-commands" /></div>
  <xsl:for-each select="multiple-command">
  	<div  class="warning"> Mehrfacher Befehl: <xsl:value-of select="text()" /> </div>
  </xsl:for-each> 
  <div>Ausserhalb des Bereichs: <xsl:value-of select="out-of-ranges" /></div>

  <h3>Code</h3>
  <div>Zeilen: <xsl:value-of select="lines" /></div>
  <div>Kommentare: <xsl:value-of select="comments" />, Zeilen-Kommentare: <xsl:value-of select="inline-comments" /></div>
  <div>Bloecke: <xsl:value-of select="blocks" />, 
        Maximale Tiefe: <xsl:value-of select="max-indent" />, 
	Methoden: <xsl:value-of select="uses-methods" />
 </div>
  <h3>Style-Check</h3>
     <xsl:for-each select="style-warning">
	<div  class="warning"> <xsl:value-of select="text()" /> </div>
	</xsl:for-each> 
  <h3>Summary</h3>
  Name = <xsl:value-of select="snippet-name" />
  N = <xsl:value-of select="symbols" />, 
  Z = <xsl:value-of select="zip-size" />,
  C = <xsl:value-of select="required-bosl-commands" />, 
  M = <xsl:value-of select="comments + inline-comments + 2 * blocks + 3 * max-indent + 3 * uses-methods" />, 
  R = <xsl:value-of select="BoSL-commands - required-bosl-commands" />, 
  S = <xsl:value-of select="count(style-warning)"/>

  <h3>CSV</h3>
  CSV <xsl:value-of select="snippet-name" />, <xsl:value-of select="symbols" />, <xsl:value-of select="zip-size" />, <xsl:value-of select="required-bosl-commands" />, <xsl:value-of select="comments + inline-comments + 2 * blocks + 3 * max-indent + 3 * uses-methods" />,  <xsl:value-of select="BoSL-commands - required-bosl-commands" />,  <xsl:value-of select="count(style-warning)"/>
  </xsl:template>

  <xsl:template match="test">
      <xsl:choose>
        <xsl:when test="hit">
          <h2><xsl:value-of  select="mode" /><xsl:text> - Solved </xsl:text></h2>
       </xsl:when>
       <xsl:otherwise>
          <h2><xsl:value-of  select="mode" /></h2>
      </xsl:otherwise>
     </xsl:choose>
    <div><img><xsl:attribute name="src"> 
           <xsl:value-of select="file" /> </xsl:attribute> 
           <xsl:attribute name="width">200</xsl:attribute> 
      </img></div>
    Level: <xsl:value-of  select="level" /> <br/>
    Time: <xsl:value-of  select="time" /> <br/>
    <xsl:for-each select="fail">
	<div  class="warning"> Fail: <xsl:value-of select="text()" /> </div>
	</xsl:for-each> 
    <xsl:for-each select="hit">
	<div style="color:green"> Hit: <xsl:value-of select="text()" /> </div>
	</xsl:for-each> 

    <xsl:for-each select="code">
	<code style="display:block;background-color:yellow;white-space:pre-wrap"><xsl:value-of select="text()" /> </code>
	</xsl:for-each> 

  </xsl:template>



</xsl:stylesheet>


