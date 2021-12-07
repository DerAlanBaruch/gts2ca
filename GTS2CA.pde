import g4p_controls.*;
import java.util.*;
import grafica.*;
GTS gts;
CA ca = null;
boolean onetime = false;
boolean onetimePlot = true;
float posX = 0;
float posY = 0;
float dzoomX = 0;
float dzoomY = 0;
boolean toDrag = false;
PGraphics pg, pg1, pg12;
float zoom = 0;
float nh = 1400;
float h = nh;
float nw = 350;
float w = nw;
boolean isTS = false;
String[] alpha;
color[] alphaColors;
int selectedColor = 0;
PGraphics pgSimulator;
int sclValue = 1;
GPlot plotFrecuency;
GPlot plotEntropy;
GPointsArray gpe;
ArrayList<GPointsArray> gpaList = new ArrayList<GPointsArray>();

java.awt.Font bold16 = new java.awt.Font("Montserrat", java.awt.Font.BOLD, 16);
java.awt.Font bold12 = new java.awt.Font("Montserrat", java.awt.Font.BOLD, 12);
java.awt.Font plain16 = new java.awt.Font("Montserrat", java.awt.Font.PLAIN, 16);
java.awt.Font plain12 = new java.awt.Font("Montserrat", java.awt.Font.PLAIN, 12);

void settings() {
  size(280, 490, JAVA2D);
}

GWindow winNewTS = null, winSimulatorCA = null, winColors = null, winStatistics = null, winTSLengthRandom = null;
GTextArea txa1;
GTextField txf1, txf2, txf3, txfw, txfh;
GSlider sdr1;
GToggleGroup togG1Options, togGSptions; 
GOption grp1_a, grp1_b, grp1_c, grp1_d; 
GDropList cdl;
GLabel label3color, label1principal;
GView viewSimulator;
GButton btnSaveImgPNG, btnSaveImgTIFF, btnSaveImgTXT, btnSaveImgJPG;
GPanel filePrincipal, saveImg, zoomPanel;
GCheckbox cbx1, cbx2;

void setup() {
  createGUI();
}

void draw() {
  background(230, 230, 255);
  noFill();
  strokeWeight(2);
  stroke(197, 206, 232);
  rect(10, 30, 260, 80);
  rect(10, 120, 260, 360);
}

void win_newts_draw(PApplet appc, GWinData data) {
  appc.background(230, 230, 255);
}

void win_colors_draw(PApplet appc, GWinData data) {
  appc.background(230, 230, 255);
}

void generateNext() {
  while (onetime && !ca.finished()) {
    ca.generate();
    ca.generatePoints();
  }
  onetime = false;
}

void win_simulatorca_draw(PApplet appc, GWinData data) {
  appc.background(255);
  if (!ca.finished()) {
    pg = ca.render();
  }
  appc.image(pg, posX, posY, w*(1+zoom), h*(1+zoom));
}

void win_statistics_draw(PApplet appc, GWinData data) {
  appc.background(255);
  appc.frameRate(5);
  float[] legendX = new float[alpha.length - 1];
  float[] legendY = new float[alpha.length - 1];
  int lY = 0;
  for (int i = 0; i < legendX.length; i++) {
    legendX[i] = 0.07 + 0.05*(i % 20);
    legendY[i] = 0.92 - 0.05*lY;
    if (i % 20 == 19) lY++;
  }
  gpaList = ca.getGPointsFrecuency();
  gpe = ca.getGPointsEntropy();
  if (onetimePlot) {
    plotFrecuency = new GPlot(appc);
    plotFrecuency.setPos(0, 0);
    plotFrecuency.setOuterDim(800, (displayHeight - 80) / 2);
    plotEntropy = new GPlot(appc);
    plotEntropy.setPos(0, (displayHeight - 80) / 2);
    plotEntropy.setOuterDim(800, (displayHeight - 80) / 2);
    plotFrecuency.setTitleText("Frecuency Chart");
    plotFrecuency.getXAxis().setAxisLabelText("Time");
    plotFrecuency.getYAxis().setAxisLabelText("Frecuency");
    plotEntropy.setTitleText("Shannon's Entropy Chart");
    plotEntropy.getXAxis().setAxisLabelText("Time");
    plotEntropy.getYAxis().setAxisLabelText("Entropy");
    plotFrecuency.activatePanning();
    plotFrecuency.activateZooming(1.2f, CENTER, CENTER);
    plotEntropy.activatePanning();
    plotEntropy.activateZooming(1.2f, CENTER, CENTER);

    plotEntropy.setPoints(gpe);
    plotEntropy.setLineColor(alphaColors[0]);
    plotEntropy.setLineWidth(2);
    plotFrecuency.setPoints(gpaList.get(1));
    plotFrecuency.setLineColor(alphaColors[1]);
    plotFrecuency.setLineWidth(2);
    for (int i = 2; i < gpaList.size(); i++) {
      plotFrecuency.addLayer(alpha[i], gpaList.get(i));
      plotFrecuency.getLayer(alpha[i]).setLineColor(alphaColors[i]);
      plotFrecuency.getLayer(alpha[i]).setLineWidth(2);
    }
    onetimePlot = false;
  }
  plotEntropy.setPoints(gpe);
  plotFrecuency.setPoints(gpaList.get(1));
  for (int i = 2; i < gpaList.size(); i++) {
    plotFrecuency.getLayer(alpha[i]).setPoints(gpaList.get(i));
  }
  plotFrecuency.beginDraw();
  plotFrecuency.drawBox();
  plotFrecuency.drawXAxis();
  plotFrecuency.drawYAxis();
  plotFrecuency.drawTitle();
  plotFrecuency.drawLines();
  if (cbx2.isSelected())
    plotFrecuency.drawLegend((new StringList(alpha)).getSubset(1).array(), legendX, legendY);
  plotFrecuency.endDraw();
  plotEntropy.beginDraw();
  plotEntropy.drawBox();
  plotEntropy.drawXAxis();
  plotEntropy.drawYAxis();
  plotEntropy.drawTitle();
  plotEntropy.drawLines();
  plotEntropy.endDraw();
}

public void SimulatorCAMouse(PApplet appc, GWinData data, MouseEvent event) {
  switch(event.getAction()) {
  case MouseEvent.PRESS:
    if (!toDrag) {
      dzoomX = appc.mouseX - posX;
      dzoomY = appc.mouseY - posY;
      toDrag = true;
    }
    break;
  case MouseEvent.RELEASE:
    if (toDrag) {
      toDrag = false;
    }
    break;
  case MouseEvent.WHEEL:
    float e = event.getCount();
    if (e > 0 && float(nf(zoom, 0, 1)) < 2) {
      zoom = zoom + 0.1;
    }
    if (e < 0 && float(nf(zoom, 0, 1)) > -0.5) {
      zoom = zoom - 0.1;
    }
    if (posX + w*(1+zoom) < 1.0)
      posX = appc.width - w*(1+zoom);
    if (posY + h*(1+zoom) < 1.0)
      posY = appc.height - h*(1+zoom);
    break;
  case MouseEvent.DRAG:
    if ((appc.mouseY - dzoomY) < 1.0 && (appc.mouseY - dzoomY + h*(1+zoom)) > appc.height) posY = appc.mouseY - dzoomY;
    else {
      if ((appc.mouseY - dzoomY) > 1.0)
        posY = 0.0;
      else if (h*(1+zoom) >= appc.height)
        posY = appc.height - h*(1+zoom);
    } 
    if ((appc.mouseX - dzoomX) < 1.0 && (appc.mouseX - dzoomX + w*(1+zoom)) > appc.width) posX = appc.mouseX - dzoomX;
    else { 
      if ((appc.mouseX - dzoomX) > 1.0)
        posX = 0.0; 
      else if (w*(1+zoom) >= appc.width)
        posX = appc.width - w*(1+zoom);
    }
    break;
  }
}

void btnNewTSCallback(GButton source, GEvent event) {
  if (winNewTS == null) {
    createNewTSWindow();
  } else {
    winNewTS.setVisible(!winNewTS.isVisible());
    winNewTS = null;
  }
}

void btnSaveImgCallback(GButton source, GEvent event) {
  String ext = "";
  if (source == btnSaveImgPNG) ext = ".png";
  if (source == btnSaveImgTIFF) ext = ".tiff";
  if (source == btnSaveImgJPG) ext = ".jpg";
  String imgPath = G4P.selectOutput("Save image as...", ext, "Image files");
  if (ca != null && imgPath != null) {
    if (imgPath.trim().toLowerCase().endsWith(ext))
      ca.render().save(imgPath);
    else
      ca.render().save(imgPath + ext);
  }
  saveImg.setCollapsed(true);
}

void btnSaveTextCallback(GButton source, GEvent event) {
  String imgPath = G4P.selectOutput("Save text as...", ".csv", "CSV files");
  if (ca != null && imgPath != null) {
    if (imgPath.trim().toLowerCase().endsWith(".csv"))
      saveStrings(imgPath, ca.getAllStrings().array());
    else
      saveStrings(imgPath + ".csv", ca.getAllStrings().array());
  }
  saveImg.setCollapsed(true);
}

void btnLengthRandomCallback(GButton source, GEvent event) {
  if (winTSLengthRandom == null) {
    createLengthRandomWindow();
  } else {
    winTSLengthRandom.close();
    winTSLengthRandom = null;
  }
}

void btnSimulatorCACallback(GButton source, GEvent event) {
  if (winSimulatorCA == null) {
    createSimulatorCAWindow();
  } else {
    winSimulatorCA.close();
    //winSimulatorCA = null;
  }
}

void closeSimulatorCACallback(GWindow window) {
  winSimulatorCA = null;
  onetime = false;
  println("Simulator closed");
}

void closeStatisticsCallback(GWindow window) {
  winStatistics = null;
  onetimePlot = true;
  println("Statistics closed");
}

void closeRandomTSCallback(GWindow window) {
  winTSLengthRandom = null;
}

void btnStatisticsCallback(GButton source, GEvent event) {
  if (winStatistics == null) {
    createStatisticsWindow();
  } else {
    winStatistics.close();
  }
}

void btnColorsCallback(GButton source, GEvent event) {
  if (winColors == null) {
    createColorsWindow();
  } else {
    winColors.setVisible(!winColors.isVisible());
    winColors = null;
  }
}

GTS makeTagSystem(String[] eb, int p, String[] rs) {
  printArray(eb);
  printArray(rs);
  GTS gts = new GTS(p, eb);
  for (int i = 0; i < eb.length; i++) {
    gts.setAppendat(0, i, rs[i]);
  }
  gts.convertToCA();
  println(gts.getRulesString());
  return gts;
}

void createTS(GButton source, GEvent event) {
  String prodRulesStr = txa1.getText().replace(" ", "");
  println(prodRulesStr);
  int p = int(txf1.getText().trim());
  println(p);
  if (prodRulesStr != "" && p != 0) {
    String[] rs = prodRulesStr.split(",");
    StringList sigma = new StringList();
    StringList appendants = new StringList();
    StringList prodRulesList = new StringList();
    for (int i = 0; i < rs.length; i++) {
      prodRulesList.append(rs[i].trim());
      String[] rn = rs[i].split("->");
      sigma.append(rn[0].trim());
      if (rn.length > 1)
        appendants.append(rn[1].trim());
      else
        appendants.append("");
    }
    pg = null;
    ca = null;
    gts = makeTagSystem(sigma.array(), p, appendants.array());
    gts.setRules(prodRulesList);
    ca = new CA(gts.getCARules(), gts.getCAAlpha());                 // Initialize CA
    String tsMsg = "TS: R=" + appendants.array().length + " P=" + p;
    String caMsg = "CA: R=" + gts.getCARules().size() + " Sigma=" + gts.getCAAlpha().size();
    StringBuilder s = new StringBuilder();
    s.append(tsMsg + "\n");
    s.append(caMsg);
    label1principal.setText(s.toString());
    println(s.toString());
    label1principal.setTextBold();
    ca.setETS(gts.getSymbols());
    alpha = ca.getAlpha();
    float diff = 1.0 / float(alpha.length);
    IntList alphaColorList = new IntList();
    for (int i = 0; i < alpha.length; i++) {
      alphaColorList.append(lerpColor(color(int(random(255)), int(random(255)), int(random(255))), color(int(random(255)), int(random(255)), int(random(255))), i*diff));
    }
    alphaColors = alphaColorList.array();
    ca.setAlphaColors(alphaColorList.array());
    isTS = true;
    winNewTS.setVisible(false);
    winNewTS = null;
  } else {
    G4P.showMessage(this, "Please, enter the production rules and deletion number P", "Data missed", G4P.ERROR_MESSAGE);
  }
}

void createNewTSWindow() {
  winNewTS = GWindow.getWindow(this, "New", 50, 50, 300, 290, JAVA2D);
  winNewTS.setActionOnClose(G4P.HIDE_WINDOW);
  winNewTS.addDrawHandler(this, "win_newts_draw");
  GLabel label1 = new GLabel(winNewTS, 10, 10, 280, 40);
  label1.setTextAlign(GAlign.LEFT, GAlign.MIDDLE);
  label1.setText("Enter the production rules and deletion number P, then clic on Create.");
  label1.setOpaque(false);
  label1.setFont(new java.awt.Font("Montserrat", java.awt.Font.PLAIN, 12));
  txa1 = new GTextArea(winNewTS, 10, 60, 280, 160);
  txa1.setPromptText("Production rules* (ej. 0->01, 1->b0)");
  txa1.setText("");
  txf1 = new GTextField(winNewTS, 10, 230, 140, 20);
  txf1.setPromptText("Deletion number P*");
  txf1.setText("");
  GButton summit = new GButton(winNewTS, 200, 260, 90, 20);
  summit.setText("Create");
  summit.addEventHandler(this, "createTS");
  filePrincipal.setCollapsed(true);
}

void createLengthRandomWindow() {
  if (isTS) {
    winTSLengthRandom = GWindow.getWindow(this, "Length of String", 50, 50, 200, 120, JAVA2D);
    winTSLengthRandom.setActionOnClose(G4P.CLOSE_WINDOW);
    winTSLengthRandom.addDrawHandler(this, "win_newts_draw");
    winTSLengthRandom.addOnCloseHandler(this, "closeRandomTSCallback");
    GLabel label1 = new GLabel(winTSLengthRandom, 10, 10, 180, 40);
    label1.setTextAlign(GAlign.LEFT, GAlign.MIDDLE);
    label1.setText("Enter a length for the random TS string:");
    label1.setOpaque(false);
    label1.setFont(new java.awt.Font("Montserrat", java.awt.Font.PLAIN, 12));
    txf3 = new GTextField(winTSLengthRandom, 10, 60, 180, 20);
    txf3.setPromptText("Length*");
    txf3.setText("");
    GButton summit = new GButton(winTSLengthRandom, 100, 90, 90, 20);
    summit.setText("OK");
    summit.addEventHandler(this, "generateRandomStringTS");
  } else {
    G4P.showMessage(this, "There is not a cellular automata, create new or open some one in File", "Cellular automata missed", G4P.ERROR_MESSAGE);
  }
}

public void handleColorChooser(GButton button, GEvent event) {
  alphaColors[selectedColor] = G4P.selectColor();
  ca.setAlphaColors(alphaColors);
  pg = ca.render();
  pg1.beginDraw();
  pg1.background(alphaColors[selectedColor]);
  pg1.endDraw();
}

public void handleColorChooserStroke(GButton button, GEvent event) {
  color colorStroke = G4P.selectColor();
  ca.setColorStroke(colorStroke);
  pg = ca.render();
  pg12.beginDraw();
  pg12.background(colorStroke);
  pg12.endDraw();
}

void btnColorsRandomizeCallback(GButton source, GEvent event) {
  float diff = 1.0 / float(alpha.length);
  IntList alphaColorList = new IntList();
  for (int i = 0; i < alpha.length; i++) {
    alphaColorList.append(lerpColor(color(int(random(255)), int(random(255)), int(random(255))), color(int(random(255)), int(random(255)), int(random(255))), i*diff));
  }
  alphaColors = alphaColorList.array();
  ca.setAlphaColors(alphaColorList.array());
  pg1.beginDraw();
  pg1.background(alphaColors[selectedColor]);
  pg1.endDraw();
  pg = ca.render();
}

void btnColorsOkCallback(GButton source, GEvent event) {
  winColors.close();
  winColors = null;
}

void alphaChooser(GDropList droplist, GEvent event) {
  selectedColor = cdl.getSelectedIndex();
  label3color.setText(alpha[selectedColor]);
  label3color.setTextBold();
  pg1.beginDraw();
  pg1.background(alphaColors[selectedColor]);
  pg1.endDraw();
}

void createColorsWindow() {
  if (isTS) {
    winColors = GWindow.getWindow(this, "Colors", 50, 50, 240, 250, JAVA2D);
    winColors.setActionOnClose(G4P.CLOSE_WINDOW);
    winColors.addDrawHandler(this, "win_colors_draw");
    GLabel label1 = new GLabel(winColors, 10, 10, 220, 40);
    label1.setTextAlign(GAlign.LEFT, GAlign.MIDDLE);
    label1.setText("Select a symbol and clic in Change to change its color.");
    label1.setOpaque(false);
    label1.setFont(plain12);
    GLabel label2 = new GLabel(winColors, 10, 50, 220, 20);
    label2.setTextAlign(GAlign.LEFT, GAlign.MIDDLE);
    label2.setText("Symbols");
    label2.setOpaque(false);
    label2.setFont(bold12);
    label2.setTextBold();
    cdl = new GDropList(winColors, 10, 70, 100, 140, 6, 15);
    cdl.setItems(alpha, 0);
    cdl.setLocalColorScheme(5);
    cdl.addEventHandler(this, "alphaChooser");
    GButton btnColor = new GButton(winColors, 120, 70, 70, 20, "Change");
    btnColor.addEventHandler(this, "handleColorChooser");
    GView view = new GView(winColors, 10, 100, 220, 20, JAVA2D);
    label3color = new GLabel(winColors, 10, 100, 220, 20);
    label3color.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
    selectedColor = 0;
    label3color.setText(alpha[selectedColor]);
    label3color.setOpaque(false);
    label3color.setFont(bold12);
    label3color.setTextBold();
    pg1 = view.getGraphics();
    pg1.beginDraw();
    pg1.background(alphaColors[selectedColor]);
    pg1.endDraw();

    GLabel label3stroke = new GLabel(winColors, 10, 130, 220, 20);
    label3stroke.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
    label3stroke.setText("Borders");
    label3stroke.setOpaque(false);
    label3stroke.setFont(bold12);
    label3stroke.setTextBold();
    GButton btnColorStroke = new GButton(winColors, 160, 160, 70, 20, "Change");
    btnColorStroke.addEventHandler(this, "handleColorChooserStroke");
    GView viewStroke = new GView(winColors, 10, 160, 140, 20, JAVA2D);
    pg12 = viewStroke.getGraphics();
    pg12.beginDraw();
    pg12.background(ca.getColorStroke());
    pg12.endDraw();

    GButton randomize = new GButton(winColors, 50, 190, 140, 20);
    randomize.setText("Randomize colors");
    randomize.addEventHandler(this, "btnColorsRandomizeCallback");

    GButton summit = new GButton(winColors, 75, 220, 90, 20);
    summit.setText("OK");
    summit.addEventHandler(this, "btnColorsOkCallback");
  } else {
    G4P.showMessage(this, "There is not a cellular automata, create new or open some one in File", "Cellular automata missed", G4P.ERROR_MESSAGE);
  }
}

void createSimulatorCAWindow() {
  if (isTS) {
    if (txf2.getText() != "") {
      nh = float(txfh.getText());
      nw = float(txfw.getText());
      println(nh);
      println(nw);
      winSimulatorCA = GWindow.getWindow(this, "SimulatorCA", 280, 0, displayWidth/2 - 280, displayHeight - 60, JAVA2D);
      winSimulatorCA.setActionOnClose(G4P.CLOSE_WINDOW);
      winSimulatorCA.performCloseAction();
      h = nh*sdr1.getValueI();
      w = nw*sdr1.getValueI();
      ca.setScale(sdr1.getValueI());
      ca.initialize(txf2.getText(), int(nw), int(nh));
      ca.setOnlyTS(cbx1.isSelected());
      posX = 0;
      posY = 0;
      dzoomX = 0;
      dzoomY = 0;
      zoom = 0;
      onetime = true;
      thread("generateNext");
      println("Init Simulator");
      winSimulatorCA.addDrawHandler(this, "win_simulatorca_draw");
      winSimulatorCA.addMouseHandler(this, "SimulatorCAMouse");
      winSimulatorCA.addOnCloseHandler(this, "closeSimulatorCACallback");
    } else {
      G4P.showMessage(this, "Enter a initial string or generate a random initial string", "Initial string missed", G4P.ERROR_MESSAGE);
    }
  } else {
    G4P.showMessage(this, "There is not a cellular automata, create new or open some one in File", "Cellular automata missed", G4P.ERROR_MESSAGE);
  }
}

void createStatisticsWindow() {
  if (isTS) {
    if (txf2.getText() != "") {
      winStatistics = GWindow.getWindow(this, "Statistics", 280, 0, 800, displayHeight - 60, JAVA2D);
      winStatistics.setActionOnClose(G4P.CLOSE_WINDOW);
      cbx2 = new GCheckbox(winStatistics, 10, 0, 160, 20, "Show Legend");
      cbx2.setSelected(false);
      onetimePlot = true;
      winStatistics.addDrawHandler(this, "win_statistics_draw");
      winStatistics.addOnCloseHandler(this, "closeStatisticsCallback");
    } else {
      G4P.showMessage(this, "Enter a initial string or generate a random initial string", "Initial string missed", G4P.ERROR_MESSAGE);
    }
  } else {
    G4P.showMessage(this, "There is not a cellular automata, create new or open some one in File", "Cellular automata missed", G4P.ERROR_MESSAGE);
  }
}

public void sdr1CallBack(GSlider slider, GEvent event) {
  if (ca !=  null && event.getType().trim().equals("RELEASED") && sclValue != sdr1.getValueI()) {
    ca.setScale(sdr1.getValueI());
    h = nh*sdr1.getValueI();
    w = nw*sdr1.getValueI();
    sclValue = sdr1.getValueI();
    pg = ca.render();
  }
}

public void handleToggleControlEvents(GOption option, GEvent event) {
  if (ca != null) {
    if (grp1_a.isSelected())
      ca.setShowStroke(true);
    if (grp1_b.isSelected())
      ca.setShowStroke(false);
    pg = ca.render();
  }
}

void saveJSON(GButton button, GEvent event) {
  JSONObject newJSON = new JSONObject();
  JSONObject tsJSON = new JSONObject();
  JSONObject caJSON = new JSONObject();
  tsJSON.setInt("P", gts.p);

  JSONArray tsSigma = new JSONArray();
  for (int i = 0; i < gts.getSymbols().length; i++)
    tsSigma.setString(i, gts.getSymbols()[i]);
  tsJSON.setJSONArray("Sigma", tsSigma);

  JSONArray tsRules = new JSONArray();
  for (int i = 0; i < gts.getRules().size(); i++)
    tsRules.setString(i, gts.getRules().get(i));
  tsJSON.setJSONArray("Rules", tsRules);

  JSONArray caSigma = new JSONArray();
  for (int i = 0; i < ca.getAlpha().length; i++)
    caSigma.setString(i, ca.getAlpha()[i]);
  caJSON.setJSONArray("Sigma", caSigma);

  JSONArray caColors = new JSONArray();
  for (int i = 0; i < alphaColors.length; i++)
    caColors.setInt(i, alphaColors[i]);
  caJSON.setJSONArray("Colors", caColors);

  JSONArray caRules = new JSONArray();
  for (int i = 0; i < gts.getCARules().size(); i++)
    caRules.setString(i, gts.getCARules().get(i));
  caJSON.setJSONArray("Rules", caRules);

  newJSON.setJSONObject("ts", tsJSON);
  newJSON.setJSONObject("ca", caJSON);
  String pathName = G4P.selectOutput("Save as JSON...", ".json", "JSON Files");
  if (pathName != null) {
    if (pathName.trim().toLowerCase().endsWith(".json"))
      saveJSONObject(newJSON, pathName);
    else
      saveJSONObject(newJSON, pathName + ".json");
  }
  filePrincipal.setCollapsed(true);
}

void openJSON(GButton button, GEvent event) {
  JSONObject newJSON = new JSONObject();
  JSONObject tsJSON = new JSONObject();
  JSONObject caJSON = new JSONObject();

  String pathName = G4P.selectInput("Open JSON...", ".json", "JSON Files");

  if (pathName != null) {
    if (ca != null) ca.reset();

    if (pathName.trim().endsWith(".json")) {
      newJSON = loadJSONObject(pathName);

      if (!newJSON.isNull("ts") && !newJSON.isNull("ca")) {
        tsJSON = newJSON.getJSONObject("ts");

        if (winSimulatorCA != null) {
          winSimulatorCA.close();
          winSimulatorCA = null;
        }

        if (winNewTS != null) {
          winNewTS.close();
          winNewTS = null;
        }

        if (winColors != null) {
          winColors.close();
          winColors = null;
        }

        if (winStatistics != null) {
          winStatistics.close();
          winStatistics = null;
        }

        onetime = true;

        int p = tsJSON.getInt("P");

        JSONArray tsSigma = new JSONArray();
        tsSigma = tsJSON.getJSONArray("Sigma");

        JSONArray tsRules = new JSONArray();
        tsRules = tsJSON.getJSONArray("Rules");

        String[] rs = tsRules.getStringArray();
        StringList appendants = new StringList();
        StringList prodRulesList = new StringList();

        for (int i = 0; i < rs.length; i++) {
          prodRulesList.append(rs[i].trim());
          String[] rn = rs[i].split("->");
          if (rn.length > 1)
            appendants.append(rn[1].trim());
          else
            appendants.append("");
        }
        pg = null;
        ca = null;
        gts = makeTagSystem(tsSigma.getStringArray(), p, appendants.array());
        gts.setRules(prodRulesList);

        caJSON = newJSON.getJSONObject("ca");

        JSONArray caSigma = new JSONArray();
        caSigma = caJSON.getJSONArray("Sigma");

        JSONArray caRules = new JSONArray();
        caRules = caJSON.getJSONArray("Rules");

        JSONArray caColors = new JSONArray();
        caColors = caJSON.getJSONArray("Colors");

        ca = new CA(new StringList(caRules.getStringArray()), new StringList(caSigma.getStringArray()));                 // Initialize CA
        String tsMsg = "TS: R=" + appendants.array().length + " P=" + p;
        String caMsg = "CA: R=" + gts.getCARules().size() + " Sigma=" + gts.getCAAlpha().size();
        StringBuilder s = new StringBuilder();
        s.append(tsMsg + "\n");
        s.append(caMsg);
        label1principal.setText(s.toString());
        println(s.toString());
        label1principal.setTextBold();
        ca.setETS(gts.getSymbols());
        alpha = ca.getAlpha();
        alphaColors = caColors.getIntArray();
        ca.setAlphaColors(alphaColors);
        isTS = true;
      } else {
        G4P.showMessage(this, "This JSON file doesn't have required fields.", "No Data in JSON", G4P.ERROR_MESSAGE);
      }
    } else {
      G4P.showMessage(this, "File type not supported.", "JSON missed", G4P.ERROR_MESSAGE);
    }
  }
  filePrincipal.setCollapsed(true);
}

void generateRandomStringTS(GButton button, GEvent event) {
  gts.setRandomLen(int(txf3.getText()));
  txf2.setText(gts.randomInitialString());
  winTSLengthRandom.close();
  winTSLengthRandom = null;
}

void generateRandomStringCA(GButton button, GEvent event) {
  nh = float(txfh.getText());
  nw = float(txfw.getText());
  ca.initialize(txf2.getText(), int(nw), int(nh));
  txf2.setText(ca.randomInitialString());
}

void btnZoomCallback(GButton button, GEvent event) { 
  zoom = float(button.tag)/100;
  zoomPanel.setCollapsed(true);
}

void createGUI() {
  G4P.messagesEnabled(false);
  G4P.setGlobalColorScheme(GCScheme.BLUE_SCHEME);
  G4P.setMouseOverEnabled(false);
  G4P.setDisplayFont("Montserrat", G4P.BOLD, 12);
  surface.setTitle("GTS2CA 1.0");
  surface.setLocation(0, 0);

  GButton.useRoundCorners(false);

  filePrincipal = new GPanel(this, 0, 0, 35, 20);
  filePrincipal.setText("File     ...");
  filePrincipal.setCollapsed(true);
  filePrincipal.setDraggable(false);
  GButton btnOpenTSwindow = new GButton(this, 0, 20, 60, 20);
  btnOpenTSwindow.setText("Open");
  btnOpenTSwindow.addEventHandler(this, "openJSON");
  GButton btnNewTSwindow = new GButton(this, 0, 40, 60, 20);
  btnNewTSwindow.setText("New");
  btnNewTSwindow.addEventHandler(this, "btnNewTSCallback");
  GButton btnSaveTSwindow = new GButton(this, 0, 60, 60, 20);
  btnSaveTSwindow.setText("Save");
  btnSaveTSwindow.addEventHandler(this, "saveJSON");
  filePrincipal.addControl(btnNewTSwindow);
  filePrincipal.addControl(btnSaveTSwindow);
  filePrincipal.addControl(btnOpenTSwindow);

  saveImg = new GPanel(this, 36, 0, 50, 20);
  saveImg.setText("Export     ...");
  saveImg.setCollapsed(true);
  saveImg.setDraggable(false);
  btnSaveImgPNG = new GButton(this, 0, 20, 100, 20);
  btnSaveImgPNG.setText("PNG Image");
  btnSaveImgPNG.addEventHandler(this, "btnSaveImgCallback");
  btnSaveImgJPG = new GButton(this, 0, 40, 100, 20);
  btnSaveImgJPG.setText("JPG Image");
  btnSaveImgJPG.addEventHandler(this, "btnSaveImgCallback");
  btnSaveImgTIFF = new GButton(this, 0, 60, 100, 20);
  btnSaveImgTIFF.setText("TIFF Image");
  btnSaveImgTIFF.addEventHandler(this, "btnSaveImgCallback");
  btnSaveImgTXT = new GButton(this, 0, 80, 100, 20);
  btnSaveImgTXT.setText("Text");
  btnSaveImgTXT.addEventHandler(this, "btnSaveTextCallback");
  saveImg.addControl(btnSaveImgPNG);
  saveImg.addControl(btnSaveImgJPG);
  saveImg.addControl(btnSaveImgTIFF);
  saveImg.addControl(btnSaveImgTXT);

  zoomPanel = new GPanel(this, 87, 0, 45, 20);
  zoomPanel.setText("Zoom     ...");
  zoomPanel.setCollapsed(true);
  zoomPanel.setDraggable(false);

  GButton[] btnZoom = new GButton[7];
  for (int i = 0; i < 7; i++) {
    btnZoom[i] = new GButton(this, 0, 20 + i*20, 45, 20);
    btnZoom[i].setText(str(50 + i*25) + "%");
    btnZoom[i].addEventHandler(this, "btnZoomCallback");
    btnZoom[i].tag = str(-50 + i*25);
    zoomPanel.addControl(btnZoom[i]);
  }

  label1principal = new GLabel(this, 20, 40, 240, 60);
  label1principal.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
  label1principal.setText("There is not a Tag System, press File to enter a Tag System");
  label1principal.setFont(plain16);

  GLabel label21 = new GLabel(this, 20, 130, 100, 20);
  label21.setTextAlign(GAlign.LEFT, GAlign.MIDDLE);
  label21.setText("Simulation");
  label21.setOpaque(false);
  label21.setTextBold();

  txf2 = new GTextField(this, 20, 160, 240, 20);
  txf2.setPromptText("Initial String");
  txf2.setText("");

  GButton btnRandomTS = new GButton(this, 20, 190, 115, 20);
  btnRandomTS.setText("Random TS Str");
  btnRandomTS.addEventHandler(this, "btnLengthRandomCallback");

  GButton btnRandomCA = new GButton(this, 145, 190, 115, 20);
  btnRandomCA.setText("Random CA Str");
  btnRandomCA.addEventHandler(this, "generateRandomStringCA");

  int l2sx = 20, l2sy = 220;
  GLabel label2 = new GLabel(this, l2sx, l2sy, 70, 20);
  label2.setTextAlign(GAlign.LEFT, GAlign.MIDDLE);
  label2.setText("Scale");
  label2.setOpaque(false);

  sdr1 = new GSlider(this, l2sx, l2sy, 140, 80, 10); 
  sdr1.setLimits(2, 1, 10);
  sdr1.setLocalColorScheme(6); 
  sdr1.setOpaque(false);  
  sdr1.setNbrTicks(10); 
  sdr1.setShowLimits(true); 
  sdr1.setShowValue(true); 
  sdr1.setShowTicks(true); 
  sdr1.setStickToTicks(true); 
  sdr1.setEasing(10); 
  sdr1.setRotation(0.0, GControlMode.CENTER);
  sdr1.addEventHandler(this, "sdr1CallBack");

  int l3tx = 180, l3ty = 220;
  GLabel label3 = new GLabel(this, l3tx, l3ty, 70, 20);
  label3.setTextAlign(GAlign.LEFT, GAlign.MIDDLE);
  label3.setText("Borders");
  label3.setOpaque(false);

  togG1Options = new GToggleGroup();
  grp1_a = new GOption(this, l3tx, l3ty + 20, 120, 20);
  grp1_a.setTextAlign(GAlign.LEFT, GAlign.MIDDLE);
  grp1_a.setText("Yes");
  grp1_a.addEventHandler(this, "handleToggleControlEvents");
  grp1_b = new GOption(this, l3tx, l3ty + 40, 120, 20);
  grp1_b.setTextAlign(GAlign.LEFT, GAlign.MIDDLE);
  grp1_b.setText("No");
  grp1_b.addEventHandler(this, "handleToggleControlEvents");
  togG1Options.addControl(grp1_a);
  togG1Options.addControl(grp1_b);
  grp1_b.setSelected(true);

  GLabel label4 = new GLabel(this, 20, 300, 50, 20);
  label4.setTextAlign(GAlign.LEFT, GAlign.MIDDLE);
  label4.setText("Cells:");
  label4.setOpaque(false);
  txfw = new GTextField(this, 70, 300, 110, 20);
  txfw.setPromptText("No. of Cells");
  txfw.setText("300");

  GLabel label5 = new GLabel(this, 20, 330, 90, 20);
  label5.setTextAlign(GAlign.LEFT, GAlign.MIDDLE);
  label5.setText("Evolutions:");
  label5.setOpaque(false);
  txfh = new GTextField(this, 105, 330, 140, 20);
  txfh.setPromptText("No. of Evolutions");
  txfh.setText("1000");

  cbx1 = new GCheckbox(this, 20, 360, 180, 18, "Only TS Alphabet Strings");
  cbx1.setSelected(false);

  GButton btnColors = new GButton(this, 70, 390, 140, 20);
  btnColors.setText("Colors Setup");
  btnColors.addEventHandler(this, "btnColorsCallback");

  GButton btnStatistics = new GButton(this, 80, 420, 120, 20);
  btnStatistics.setText("See Stats");
  btnStatistics.addEventHandler(this, "btnStatisticsCallback");

  GButton btnSimulatorCA = new GButton(this, 80, 450, 120, 20);
  btnSimulatorCA.setText("Run Simulation");
  btnSimulatorCA.addEventHandler(this, "btnSimulatorCACallback");
}
