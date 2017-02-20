import java.util.HashMap;

import org.openscience.cdk.io.SDFWriter
import org.openscience.cdk.smiles.SmilesParser
import org.openscience.cdk.exception.InvalidSmilesException
import org.openscience.cdk.silent.SilentChemObjectBuilder

// read Wikidata <> SMILES
smilesMap = new HashMap()
inchiMap = new HashMap()
smilesFile = new File("wikidata.tsv")
smilesFile.eachLine { line,number ->
  if (number == 1) return
  def (compound, inchikey, smiles) = line.split(/\t/)
  compound = compound.replace("\"","")
  smilesMap["$compound"] = smiles
  inchiMap["$compound"] = inchikey
}

// read WikiPathways metabolite pathways
pathways = new HashMap()
pathwaysFile = new File("pathways.tsv")
pathwaysFile.eachLine { line,number ->
  if (number == 1) return
  def (compound, pathwayIRI, title, organism) = line.split(/\t/)
  compound   = compound.replace("\"","")
  pathwayIRI = pathwayIRI.replace("\"","")
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

// read WikiPathways metabolites with Wikidata IDs
// for each:
//   WP metabolite, create an SD file entry

writer = new SDFWriter(new File("wikipathways.sdf").newOutputStream())

smiParser = new SmilesParser(SilentChemObjectBuilder.instance)
wpMetabolitesFile = new File("wikipathways.tsv")
wpMetabolitesFile.eachLine { line,number ->
  if (number == 1) return
  compound = line.replace("\"","")
  if (smilesMap.containsKey("" + compound)) {
    smiles = smilesMap["$compound"]
    try {
      mol = smiParser.parseSmiles(smiles)
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
      writer.write(mol)
    } catch (InvalidSmilesException invalidSmiles) {
    }
  }
}

writer.close()