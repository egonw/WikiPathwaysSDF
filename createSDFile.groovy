import java.util.HashMap;

import org.openscience.cdk.io.SDFWriter
import org.openscience.cdk.smiles.SmilesParser
import org.openscience.cdk.exception.InvalidSmilesException
import org.openscience.cdk.silent.SilentChemObjectBuilder
import org.openscience.cdk.layout.StructureDiagramGenerator

// read Wikidata <> SMILES
smilesMap = new HashMap()
inchiMap = new HashMap()
smilesFile = new File("wikidata.tsv")
smilesFile.eachLine { line,number ->
  if (number == 1) return
  def fields = line.split(/\t/)
  if (fields.length > 3) {
    def (compound, inchikey, smiles, chiral) = fields
    compound = compound.replace("\"","")
    smilesMap["$compound"] = chiral
    inchiMap["$compound"] = inchikey
  } else {
    def (compound, inchikey, smiles) = fields
    compound = compound.replace("\"","")
    smilesMap["$compound"] = smiles
    inchiMap["$compound"] = inchikey
  }
}

// read WikiPathways metabolite pathways
pathways = new HashMap()
pathwaysFile = new File("pathways.tsv")
pathwaysFile.eachLine { line,number ->
  if (number == 1) return
  def (compound, pathwayIRI, title, organism) = line.split(/\t/)
  compound   = compound.replace("\"","")
  pathwayIRI = pathwayIRI.replace("\"","").replace("<","").replace(">","")
  title      = title.replace("\"","")
  organism   = organism.replace("\"","")
  pathwayList = pathways.get("" + compound)
  if (pathwayList == null) {
    pathwayList = new HashMap()
    pathways["$compound"] = pathwayList
  }
  pathwayList["$pathwayIRI"] = title + " (" + organism + ")"
}

// read WikiPathways metabolite labels
labels = new HashMap()
labelsFile = new File("labels.tsv")
labelsFile.eachLine { line,number ->
  if (number == 1) return
  def (compound, title) = line.split(/\t/)
  compound   = compound.replace("\"","")
  title      = title.replace("\"","")
  labelsList = labels.get("" + compound)
  if (labelsList == null) {
    labelsList = new ArrayList()
    labels["$compound"] = labelsList
  }
  labelsList.add(title)
}

// read WikiPathways metabolite IRIs
iris = new HashMap()
irisFile = new File("iris.tsv")
irisFile.eachLine { line,number ->
  if (number == 1) return
  def (compound, iri) = line.split(/\t/)
  compound = compound.replace("\"","")
  iri      = iri.replace("\"","")
  irisList = iris.get("" + compound)
  if (irisList == null) {
    irisList = new ArrayList()
    iris["$compound"] = irisList
  }
  irisList.add(iri)
}

// read WikiPathways metabolites with Wikidata IDs
// for each:
//   WP metabolite, create an SD file entry

writer = new SDFWriter(new File("wikipathways.sdf").newOutputStream())

sdg = new StructureDiagramGenerator();
smiParser = new SmilesParser(SilentChemObjectBuilder.instance)
wpMetabolitesFile = new File("wikipathways.tsv")
wpMetabolitesFile.eachLine { line,number ->
  if (number == 1) return
  compound = line.replace("\"","")
  if (smilesMap.containsKey("" + compound)) {
    smiles = smilesMap["$compound"]
    try {
      mol = smiParser.parseSmiles(smiles)
      sdg.generateCoordinates(mol);
      mol.setProperty("cdk:Title", "http://www.wikidata.org/entity/" + compound)
      pathwayList = pathways.get("" + compound)
      if (pathwayList != null) {
        for (iri in pathwayList.keySet()) {
          mol.setProperty(iri, pathwayList.get(iri))
        }
      }
      labelList = labels.get("" + compound)
      if (labelList != null) {
        labelCounter = 0
        for (label in labelList) {
          mol.setProperty("Label" + labelCounter, label)
          labelCounter++
        }
      }
      irisList = iris.get("" + compound)
      if (irisList != null) {
        iriCounter = 0
        for (iri in irisList) {
          mol.setProperty("IRI" + iriCounter, iri)
          iriCounter++
        }
      }
      writer.write(mol)
    } catch (InvalidSmilesException invalidSmiles) {
    }
  }
}

writer.close()