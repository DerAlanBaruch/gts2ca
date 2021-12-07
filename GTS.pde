class GTS {
  String[][] rules;
  int p, l;
  String actual;
  color[] cr = {#77E6D9, #E58860};
  String[] e;
  int c = 0;
  int t = 0;
  int tt = 0;
  int m = 0;
  int scl = 1;
  int y = 0;
  StringList caRules = new StringList();
  String[] ect = {"N", "U", "L"};
  StringList eca = new StringList(ect);
  StringList productionRules;
  int randomLen = 10;

  GTS(int _p, String[] _e) {
    p = _p;
    e = _e;
    l = e.length;
    rules = new String[p][l];
    //cr = new color[l];
    //initColors();
    initRules();
  }

  GTS(int _p) {
    p = _p;
  }

  GTS() {
  }

  void setMode(int _m) {
    m = _m;
  }

  void setRandomLen(int _rlen) {
    randomLen = _rlen;
  }

  StringList getCARules() {
    return caRules;
  }

  StringList getCAAlpha() {
    return eca;
  }

  StringList getRules() {
    return productionRules;
  }

  String randomInitialString() {
    String randomInit = "";
    for (int i = 0; i < randomLen; i++) {
      randomInit = randomInit + e[int(random(e.length))];
    }
    return randomInit;
  }

  void setRules(StringList _productionRules) {
    productionRules = _productionRules;
  }

  void initRules() {
    for (int i = 0; i < p; i++) {
      for (int j = 0; j < l; j++) {
        rules[i][j] = "";
      }
    }
  }

  void setScale(int _s) {
    scl = _s;
  }

  void initColors() {
    float n = parseFloat(l) - 2.0;
    cr[0] = color(255, 255, 255);
    cr[cr.length - 1] = color(0, 0, 0);
    if (n > 0) {
      float a = 255.0/(n + 1.0);
      for (int i = 1; i < cr.length - 1; i++) {
        cr[i] = color(int(i*a), int(i*a), int(i*a));
      }
    }
  }

  String[] getSymbols() {
    return e;
  }

  void setSymbols(String[] _e) {
    e = _e;
    l = e.length;
    rules = new String[p][l];
    initRules();
  }

  void setAppendat(int i, int j, String a) {
    rules[i][j] = a;
  }

  void printRules() {
    for (int i = 0; i < p; i++) {
      printArray(rules[i]);
    }
  }

  void setInitialString(String init) {
    actual = init;
  }

  String getActualString() {
    return actual;
  }

  int getMode() {
    return m;
  }

  void generate() {
    if (actual.length() >= 1) {
      String cs = actual.substring(0, 1);
      if (m == 0) {
        if (c == 0 && actual.length() >= p) {
          tt = tt + 1;
          y = tt;
          for (int i = 0; i < e.length; i++) {
            if (e[i].equals(cs)) {
              actual = actual.substring(p, actual.length()) + rules[c][i];
            }
          }
        }
      } else {
        y = t;
        for (int i = 0; i < e.length; i++) {
          if (e[i].equals(cs)) {
            actual = actual.substring(1, actual.length()) + rules[c][i];
          }
        }
      }
      t = t + 1;
      c = t % p;
    }
  }  

  float entropy() {
    float n = 0.0;
    float pr = 0.0;
    float en = 0.0;
    for (int j = 0; j < e.length; j++) {
      for (int i = 0; i < actual.length(); i++) {
        if (str(actual.charAt(i)).equals(e[j])) {
          n = n + 1.0;
        }
      }
      pr = n/parseFloat(actual.length());
      en = en + log(pow(1/pr, pr))/log(l);
      n = 0.0;
    }
    return en;
  }

  boolean halt() {
    if (actual.length() < 1 || y*scl > height) {
      return true;
    }
    return false;
  }

  int getSteps() {
    return y;
  }

  color[] getColors() {
    return cr;
  }

  String getRulesString() {
    String ru = "" + p + "-";
    for (int i = 0; i < p; i++) {
      for (int j = 0; j < l; j++) {
        ru = ru + "(" + i + "," + e[j] +")->" + rules[i][j] + ",";
      }
    }
    return ru;
  }

  void convertToCA() {
    String[] dic = {"¡", "¢", "£", "¤", "¥", "¦", "§", "¨", "©", "ª", "«", "¬", "®", "¯", "°", "±", "²", "³", "´", "µ", "¶", "·", "¸", "¹", "º", "»", "¼", "½", "¾", "¿", "À", "Á", "Â", "Ã", "Ä", "Å", "Æ", "Ç", "È", "É", "Ê", "Ë", "Ì", "Í", "Î", "Ï", "Ð", "Ñ", "Ò", "Ó", "Ô", "Õ", "Ö", "×", "Ø", "Ù", "Ú", "Û", "Ü", "Ý", "Þ", "ß", "á", "â", "ã", "ä", "å", "æ", "ç", "è", "é", "ê", "ë", "ì", "í", "î", "ï", "ð", "ñ", "ò", "ó", "ô", "õ", "ö", "÷", "ø", "ù", "ú", "û", "ü", "ý", "þ", "ÿ", "Ā", "ā", "Ă", "ă", "Ą", "ą", "Ć", "ć", "Ĉ", "ĉ", "Ċ", "ċ", "Č", "č", "Ď", "ď", "Đ", "đ", "Ē", "ē", "Ĕ", "Ê", "ê", "Ë", "ë"};
    StringList ets = new StringList(e);
    StringList ep = new StringList();
    StringList es = new StringList();
    StringList ea = new StringList();

    int ic = 0;
    int ia = 0;
    eca.append(ets);
    for (int i = 0; i < l; i++) {
      es.appendUnique(dic[ic]);
      ic = ic + 1;
    }

    for (int i = 0; i < l*(p - 1); i++) {
      ep.appendUnique(dic[ic]);
      ic = ic + 1;
    }

    for (int i = 0; i< rules[0].length; i++) {
      for (int j = 0; j< rules[0][i].length() - 1; j++) {
        ea.appendUnique(dic[ic]);
        ic = ic + 1;
      }
    }

    eca.append(es);
    eca.append(ep);
    eca.append(ea);

    if (ep.size() > 0)
      caRules.append("N[" + join(ep.array(), "") + "].{1}->N");
    caRules.append(".{1}N[" + join(es.array(), "") + "]->L");
    caRules.append(".{1}L[" + join(ets.array(), "") + "]->L");
    caRules.append("NN[NL" + join(ets.array(), "") + join(ep.array(), "") + "]->N");
    caRules.append("[U" + join(ets.array(), "") + join(ep.array(), "") + "]N[NL]->N");
    caRules.append("[L" + join(ets.array(), "") + "][" + join(ets.array(), "") + "]U->U");
    caRules.append("LU[" + join(ets.array(), "") + "]->N");
    caRules.append(".{1}LU->N");

    for (int i = 0; i < ets.size(); i++) {
      caRules.append("[LU" + join(ets.array(), "") + "]" + ets.get(i) + "[NL" + join(ets.array(), "") + join(es.array(), "") + join(ea.array(), "") + "]->" + ets.get(i));
      caRules.append(".{1}[" + join(es.array(), "") + "]" + ets.get(i) + "->" + ets.get(i));
      caRules.append(ets.get(i) + "U.{1}->" + ets.get(i));
      if (ep.size() > 0)
        caRules.append("N" + ets.get(i) + ".{1}->" + ep.get(i));
      else
        caRules.append("N" + ets.get(i) + ".{1}->" + es.get(i));
    }

    StringList eps = new StringList(ep);
    eps.append(es);
    for (int i = 0; i < ep.size(); i++) {
      caRules.append(eps.get(i) + ".{2}->" + eps.get(i + l));
    }
    for (int i = 0; i < es.size(); i++) {
      caRules.append(es.get(i) + "[" + join(ets.array(), "") + "].{1}->" + es.get(i));
    }

    int k = 0;
    for (int i = 0; i< rules[0].length; i++) {
      if (rules[0][i].length() > 0) {
        for (int j = 0; j < rules[0][i].length(); j++) {
          if (j == 0) {
            if (rules[0][i].length() == 1) caRules.append(es.get(i) + "[NL].{1}" + "->U");
            else caRules.append(es.get(i) + "[NL].{1}" + "->" + ea.get(k));
            caRules.append("[N" + join(ets.array(), "") + "]" + es.get(i) + "[NL]" + "->" + rules[0][i].charAt(j));
          } else {
            if (j == rules[0][i].length() - 1) caRules.append(ea.get(k) + "[NL].{1}" + "->U");
            else caRules.append(ea.get(k) + "[NL].{1}" + "->" + ea.get(k + 1));
            caRules.append("[N" + join(ets.array(), "") + "]" + ea.get(k) + "[NL]" + "->" + rules[0][i].charAt(j));
            k++;
          }
        }
      } else {
        caRules.append(es.get(i) + "[NL].{1}" + "->N");
        caRules.append("[N" + join(ets.array(), "") + "]" + es.get(i) + "[NL]" + "->U");
      }
    }

    println(eca);
    println(caRules);
  }
}
