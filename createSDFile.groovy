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
      writer.write(mol)
    } catch (InvalidSmilesException invalidSmiles) {
    }
  }
}

writer.close()