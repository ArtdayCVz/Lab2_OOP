<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:p="http://www.example.com/planes"
                exclude-result-prefixes="p">

  <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

  <xsl:key name="by-type" match="p:Plane" use="p:Chars/p:Type"/>

  <xsl:template match="/p:Planes">
    <PlanesByType>
      <xsl:for-each select="p:Plane[generate-id() = generate-id(key('by-type', p:Chars/p:Type)[1])]">
        <xsl:sort select="p:Chars/p:Type"/>
        <Group type="{p:Chars/p:Type}" count="{count(key('by-type', p:Chars/p:Type))}">
          <xsl:apply-templates select="key('by-type', p:Chars/p:Type)"/>
        </Group>
      </xsl:for-each>
    </PlanesByType>
  </xsl:template>

  <xsl:template match="p:Plane">
    <Plane id="{@id}">
      <xsl:apply-templates select="p:Model | p:Origin" mode="copy"/>
      <Chars>
        <xsl:apply-templates select="p:Chars/p:Seats | p:Chars/p:Ammunition" mode="copy"/>
      </Chars>
      <xsl:apply-templates select="p:Parameters | p:Price" mode="copy"/>
    </Plane>
  </xsl:template>

  <xsl:template match="p:*" mode="copy">
    <xsl:element name="{local-name()}">
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="node()" mode="copy"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="text()" mode="copy">
    <xsl:value-of select="normalize-space(.)"/>
  </xsl:template>
</xsl:stylesheet>
