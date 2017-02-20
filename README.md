# WikiPathwaysSDF
Code to create WikiPathways SD files.

The first version is available from Figshare:

* Willighagen, Egon (2015): WikiPathways SD file (v.1). figshare. https://doi.org/10.6084/m9.figshare.1335751.v1 Retrieved: 16 22, Feb 19, 2017 (GMT)

## Makefile

    curl -H "Accept: text/csv" --data-urlencode query@smiles.rq -G https://query.wikidata.org/bigdata/namespace/wdq/sparql -o wikidata.tsv
    curl -H "Accept: text/csv" --data-urlencode query@wikipathways.rq -G http://sparql.wikipathways.org/ -o wikipathways.tsv
    curl -H "Accept: text/csv" --data-urlencode query@pathways.rq -G http://sparql.wikipathways.org/ -o pathways.tsv
    curl -H "Accept: text/csv" --data-urlencode query@labels.rq -G http://sparql.wikipathways.org/ -o labels.tsv

## Excepted output

The output of the script is supposed to look like:

    http://www.wikidata.org/entity/Q11694846
      createSDFile.groovy
    
      2  1  0  0  0  0            999 V2000
        1.6500    0.0000    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0
        2.4750    0.0000    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0
      1  2  2  0  0  0  0
    M  END
    >  <http://www.wikipathways.org/index.php/Pathway:WP15>
    Selenium Pathway (Homo sapiens)
    
    >  <http://www.wikipathways.org/index.php/Pathway:WP2436>
    Dopamine metabolism (Homo sapiens)
    
    >  <Label0>
    O2
    
    >  <Label1>
    Oxygen
    
    >  <IRI0>
    http://identifiers.org/chebi/CHEBI:16852
    
    $$$$
 
