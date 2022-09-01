import java.util.Map;
import grafica.*;

class CA {
  ArrayList<GPointsArray> pointsFrecuency = new ArrayList<GPointsArray>();
  GPointsArray pointsEntropy = new GPointsArray();
  PGraphics pg;
  String[] cells;     // An array of 0s and 1s 
  int generation = 0;  // How many generations?
  int scl = 1;         // How many pixels wide/high is each cell?
  String init;
  String[] e;
  color[] ce;
  StringList all = new StringList();
  StringList allTS = new StringList();
  int h;
  int evolutions = 0;
  boolean showStroke = false;
  String imgPath = "";
  String[] ets;
  StringList rules = new StringList();
  ArrayList<String[]> rulesList = new ArrayList<String[]>();
  color colorStroke = color(32);
  HashMap<String, Integer> sigmaColor = new HashMap<String, Integer>();
  boolean onlyTS = false;

  CA() {
  }

  CA(StringList _rules, StringList alpha) {
    e = alpha.array();
    rules = _rules;
    for (int i = 0; i < rules.size(); i++) {
      String[] ru = rules.get(i).split("->");
      rulesList.add(ru);
    }
  }

  void setETS(String[] _ets) {
    ets = _ets;
  }

  void setPGraphics(PGraphics _pg) {
    pg = _pg;
  }

  void setOnlyTS(boolean _ots) {
    onlyTS = _ots;
  }

  String[] getAlpha() {
    return e;
  }

  color[] getAlphaColors() {
    return ce;
  }

  void setAlphaColors(color[] _ce) {
    ce = _ce;
    makeSigmaColorHash();
  }

  String executeRules (String strp, String a) {
    //rules.shuffle();
    for (String[] rule : rulesList) {
      if (match(strp, rule[0]) != null) {
        return rule[1];
      }
    }
    return a;
  }

  String randomInitialString() {
    String randomInit = "";
    for (int i = 0; i < cells.length; i++) {
      randomInit = randomInit + e[int(random(e.length))];
    }
    return randomInit;
  }

  void initialize(String _init, int _w, int _h) {
    all = new StringList();
    all.clear();
    allTS = new StringList();
    allTS.clear();
    cells = new String[_w];
    init = _init;
    generation = 0;
    evolutions = 0;
    h = _h;
    if (cells.length > init.length()) {
      cells[0] = "N";
      for (int i = 1; i < init.length() + 1; i++) {
        cells[i] = str(init.charAt(i - 1));
      }
      for (int i = init.length() + 1; i < cells.length; i++) {
        cells[i] = "N";
      }
    } else {
      for (int i = 0; i < cells.length; i++) {
        cells[i] = str(init.charAt(i));
      }
    }
  }

  void generatePoints() {
    pointsFrecuency = new ArrayList<GPointsArray>();
    pointsEntropy = new GPointsArray();
    for (int i = 0; i < e.length; i++) {
      pointsFrecuency.add(new GPointsArray());
    }

    StringList toAll = new StringList();
    if (onlyTS) toAll = allTS;
    else toAll = all;
    int allSize = toAll.size();
    for (int j = 0; j < allSize; j++) {
      StringList cArray = new StringList(split(toAll.get(j), ","));
      IntDict tally = cArray.getTally();
      float h = 0.0;
      for (int k = 0; k < e.length; k++) {
        GPointsArray gpf = pointsFrecuency.get(k);
        if (tally.hasKey(e[k])) {
          gpf.add(j + 1, tally.get(e[k]));
          if (!e[k].equals("N")) {
            float p;
            if (tally.hasKey("N"))
              p = float(tally.get(e[k]))/float(cells.length - tally.get("N"));
            else
              p = float(tally.get(e[k]))/float(cells.length);
            //println(e[k] + " = " + p);
            h = h - p*log(p)/log(2);
          }
        } else {
          gpf.add(j + 1, 0);
        }
      }
      pointsEntropy.add(j + 1, h);
    }
  }

  ArrayList<GPointsArray> getGPointsFrecuency() {
    return pointsFrecuency;
  }

  GPointsArray getGPointsEntropy() {
    return pointsEntropy;
  }

  // The process of creating the new generation
  void generate() {
    String stringcells = join(cells, ",");
    evolutions++;
    if (onlyTS) {
      if (match(join(cells, ""), "[N" + join(ets, "") + "]{"+cells.length+"}") != null) {
        allTS.append(stringcells);
        generation++;
        println(evolutions);
      }
    } else {
      all.append(stringcells);
      generation++;
    }
    String[] nextgen = new String[cells.length];
    //println(join(cells, "&")+"\\\\\\hline");
    for (int i = 0; i < cells.length; i++) {
      String left = cells[(cells.length + i - 1) % cells.length];
      String right = cells[(i + 1) % cells.length];
      nextgen[i] = executeRules(left + cells[i] + right, left); // Compute next generation state based on ruleset
    }
    for (int i = 0; i < cells.length; i++) {
      cells[i] = nextgen[i];
    }
  }

  PGraphics getPG() {
    return pg;
  }
  
  int getEvolutions() {
    return evolutions;
  }

  void setScale(int _scl) {
    scl = _scl;
  }

  void setShowStroke(boolean _show) {
    showStroke = _show;
  }

  void setImgPath(String _path) {
    imgPath = _path;
  }

  void saveEvolutions() {
    saveStrings("frame-" + frameCount + ".txt", all.array());
  }

  void setColorStroke(color _cs) {
    colorStroke = _cs;
  }

  color getColorStroke() {
    return colorStroke;
  }

  void makeSigmaColorHash() {
    for (int j = 0; j < e.length; j++) {
      sigmaColor.put(e[j], ce[j]);
    }
  }

  StringList getAllStrings() {
    StringList toAll = new StringList();
    if (onlyTS) toAll = allTS;
    else toAll = all;
    return toAll;
  }

  PGraphics render() {
    int ge = 0;
    StringList toAll = new StringList();
    if (onlyTS) toAll = allTS;
    else toAll = all;
    int allSize = toAll.size();
    pg = createGraphics(cells.length*scl, h*scl);
    pg.beginDraw();
    for (int j = 0; j < allSize; j++) {
      String[] cArray = split(toAll.get(j), ",");
      for (int i = 0; i < cArray.length; i++) {
        if (scl > 1) {
          pg.fill(sigmaColor.get(cArray[i]));
          if (showStroke)
            pg.stroke(colorStroke);
          else pg.noStroke();
          pg.rect(i*scl, ge*scl, scl, scl);
        } else {
          pg.set(i, ge, sigmaColor.get(cArray[i]));
        }
      }
      ge++;
    }
    pg.endDraw();
    return pg;
  }

  void reset() {
    pointsFrecuency = new ArrayList<GPointsArray>();
    pointsEntropy = new GPointsArray();
    pg = null;
    generation = 0;  // How many generations?
    all = new StringList();
  }

  boolean finished() {
    if (generation > h) {
      return true;
    } else {
      return false;
    }
  }
}
