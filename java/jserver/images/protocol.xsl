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

  <xsl:template match="test">
      <xsl:choose>
        <xsl:when test="hit">
          <h2><xsl:text>Pattern  - Solved </xsl:text></h2>
       </xsl:when>
       <xsl:otherwise>
          <h2>Pattern</h2>
      </xsl:otherwise>
     </xsl:choose>
    <div><img><xsl:attribute name="src"> 
           <xsl:value-of select="file" /> </xsl:attribute> 
           <xsl:attribute name="width">200</xsl:attribute> 
      </img></div>
    Level: <xsl:value-of  select="level" /> <br/>
    Time: <xsl:value-of  select="time" /> <br/>
    <xsl:for-each select="fail">
	<div style="color:red"> Fail: <xsl:value-of select="text()" /> </div>
	</xsl:for-each> 
    <xsl:for-each select="hit">
	<div style="color:green"> Hit: <xsl:value-of select="text()" /> </div>
	</xsl:for-each> 

    <xsl:for-each select="code">
	<code style="display:block;background-color:yellow;white-space:pre-wrap"><xsl:value-of select="text()" /> </code>
	</xsl:for-each> 

  </xsl:template>



</xsl:stylesheet>


