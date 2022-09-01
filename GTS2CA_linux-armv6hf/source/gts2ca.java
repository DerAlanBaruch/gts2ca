import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import g4p_controls.*; 
import java.util.*; 
import grafica.*; 
import java.util.Map; 
import grafica.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class gts2ca extends PApplet {




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
int[] alphaColors;
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

public void settings() {
  size(280, 490, JAVA2D);
}

GWindow winNewTS = null, winSimulatorCA = null, winColors = null, winStatistics = null, winAbout = null, winTSLengthRandom = null;
GTextArea txa1;
GTextField txf1, txf2, txf3, txfw, txfh, txtabout;
GSlider sdr1;
GToggleGroup togG1Options, togGSptions; 
GOption grp1_a, grp1_b, grp1_c, grp1_d; 
GDropList cdl;
GLabel label3color, label1principal;
GView viewSimulator;
GButton btnSaveImgPNG, btnSaveImgTIFF, btnSaveImgTXT, btnSaveImgJPG;
GPanel filePrincipal, saveImg, zoomPanel;
GCheckbox cbx1, cbx2;

public void setup() {
  createGUI();
}

public void draw() {
  background(230, 230, 255);
  noFill();
  strokeWeight(2);
  stroke(197, 206, 232);
  rect(10, 30, 260, 80);
  rect(10, 120, 260, 360);
}

public void win_newts_draw(PApplet appc, GWinData data) {
  appc.background(230, 230, 255);
}

public void win_colors_draw(PApplet appc, GWinData data) {
  appc.background(230, 230, 255);
}

public void win_about_draw(PApplet appc, GWinData data) {
  appc.background(230, 230, 255);
}

public void generateNext() {
  while (onetime && !ca.finished()) {
    ca.generate();
    ca.generatePoints();
  }
  onetime = false;
}

public void win_simulatorca_draw(PApplet appc, GWinData data) {
  appc.background(255);
  if (!ca.finished()) {
    pg = ca.render();
  }
  appc.image(pg, posX, posY, w*(1+zoom), h*(1+zoom));
}

public void win_statistics_draw(PApplet appc, GWinData data) {
  appc.background(255);
  appc.frameRate(5);
  float[] legendX = new float[alpha.length - 1];
  float[] legendY = new float[alpha.length - 1];
  int lY = 0;
  for (int i = 0; i < legendX.length; i++) {
    legendX[i] = 0.07f + 0.05f*(i % 20);
    legendY[i] = 0.92f - 0.05f*lY;
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
    if (e > 0 && PApplet.parseFloat(nf(zoom, 0, 1)) < 2) {
      zoom = zoom + 0.1f;
    }
    if (e < 0 && PApplet.parseFloat(nf(zoom, 0, 1)) > -0.5f) {
      zoom = zoom - 0.1f;
    }
    if (posX + w*(1+zoom) < 1.0f)
      posX = appc.width - w*(1+zoom);
    if (posY + h*(1+zoom) < 1.0f)
      posY = appc.height - h*(1+zoom);
    break;
  case MouseEvent.DRAG:
    if ((appc.mouseY - dzoomY) < 1.0f && (appc.mouseY - dzoomY + h*(1+zoom)) > appc.height) posY = appc.mouseY - dzoomY;
    else {
      if ((appc.mouseY - dzoomY) > 1.0f)
        posY = 0.0f;
      else if (h*(1+zoom) >= appc.height)
        posY = appc.height - h*(1+zoom);
    } 
    if ((appc.mouseX - dzoomX) < 1.0f && (appc.mouseX - dzoomX + w*(1+zoom)) > appc.width) posX = appc.mouseX - dzoomX;
    else { 
      if ((appc.mouseX - dzoomX) > 1.0f)
        posX = 0.0f; 
      else if (w*(1+zoom) >= appc.width)
        posX = appc.width - w*(1+zoom);
    }
    break;
  }
}

public void btnNewTSCallback(GButton source, GEvent event) {
  if (winNewTS == null) {
    createNewTSWindow();
  } else {
    winNewTS.setVisible(!winNewTS.isVisible());
    winNewTS = null;
  }
}

public void btnSaveImgCallback(GButton source, GEvent event) {
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

public void btnSaveTextCallback(GButton source, GEvent event) {
  String imgPath = G4P.selectOutput("Save text as...", ".csv", "CSV files");
  if (ca != null && imgPath != null) {
    if (imgPath.trim().toLowerCase().endsWith(".csv"))
      saveStrings(imgPath, ca.getAllStrings().array());
    else
      saveStrings(imgPath + ".csv", ca.getAllStrings().array());
  }
  saveImg.setCollapsed(true);
}

public void btnLengthRandomCallback(GButton source, GEvent event) {
  if (winTSLengthRandom == null) {
    createLengthRandomWindow();
  } else {
    winTSLengthRandom.close();
    winTSLengthRandom = null;
  }
}

public void btnSimulatorCACallback(GButton source, GEvent event) {
  if (winSimulatorCA == null) {
    createSimulatorCAWindow();
  } else {
    winSimulatorCA.close();
    //winSimulatorCA = null;
  }
}

public void closeSimulatorCACallback(GWindow window) {
  winSimulatorCA = null;
  onetime = false;
  println("Simulator closed");
}

public void closeStatisticsCallback(GWindow window) {
  winStatistics = null;
  onetimePlot = true;
  println("Statistics closed");
}

public void closeRandomTSCallback(GWindow window) {
  winTSLengthRandom = null;
}

public void closeAboutCallback(GWindow window) {
  winAbout = null;
}

public void btnStatisticsCallback(GButton source, GEvent event) {
  if (winStatistics == null) {
    createStatisticsWindow();
  } else {
    winStatistics.close();
  }
}

public void btnAboutCallback(GButton source, GEvent event) {
  if (winAbout == null) {
    createAboutWindow();
  } else {
    winAbout.close();
  }
}

public void btnColorsCallback(GButton source, GEvent event) {
  if (winColors == null) {
    createColorsWindow();
  } else {
    winColors.setVisible(!winColors.isVisible());
    winColors = null;
  }
}

public GTS makeTagSystem(String[] eb, int p, String[] rs) {
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

public void createTS(GButton source, GEvent event) {
  String prodRulesStr = txa1.getText().replace(" ", "");
  println(prodRulesStr);
  int p = PApplet.parseInt(txf1.getText().trim());
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
    float diff = 1.0f / PApplet.parseFloat(alpha.length);
    IntList alphaColorList = new IntList();
    for (int i = 0; i < alpha.length; i++) {
      alphaColorList.append(lerpColor(color(PApplet.parseInt(random(255)), PApplet.parseInt(random(255)), PApplet.parseInt(random(255))), color(PApplet.parseInt(random(255)), PApplet.parseInt(random(255)), PApplet.parseInt(random(255))), i*diff));
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

public void createNewTSWindow() {
  winNewTS = GWindow.getWindow(this, "New", 50, 50, 300, 290, JAVA2D);
  winNewTS.setActionOnClose(G4P.HIDE_WINDOW);
  winNewTS.addDrawHandler(this, "win_newts_draw");
  GLabel label1 = new GLabel(winNewTS, 10, 10, 280, 40);
  label1.setTextAlign(GAlign.LEFT, GAlign.MIDDLE);
  label1.setText("Enter the production rules and deletion number P, then clic on Create.");
  label1.setOpaque(false);
  label1.setFont(new java.awt.Font("Montserrat", java.awt.Font.PLAIN, 12));
  txa1 = new GTextArea(winNewTS, 10, 60, 280, 160);
  txa1.setPromptText("Production rules* (ej. 0->01, 1->10)");
  txa1.setText("");
  txf1 = new GTextField(winNewTS, 10, 230, 140, 20);
  txf1.setPromptText("Deletion number P*");
  txf1.setText("");
  GButton summit = new GButton(winNewTS, 200, 260, 90, 20);
  summit.setText("Create");
  summit.addEventHandler(this, "createTS");
  filePrincipal.setCollapsed(true);
}

public void createAboutWindow() {
  winAbout = GWindow.getWindow(this, "About GTS2CA", 50, 50, 270, 130, JAVA2D);
  winAbout.setActionOnClose(G4P.CLOSE_WINDOW);
  winAbout.addDrawHandler(this, "win_about_draw");
  winAbout.addOnCloseHandler(this, "closeAboutCallback");
  GLabel label4 = new GLabel(winAbout, 20, 20, 230, 90);
  label4.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
  label4.setText("Dev: Alan Baruch Cerna González\nEmail: acernag1500@alumno.ipn.mx\n \nArtificial Life Robotics Lab\nESCOM - IPN\n2022");
}

public void createLengthRandomWindow() {
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
  int colorStroke = G4P.selectColor();
  ca.setColorStroke(colorStroke);
  pg = ca.render();
  pg12.beginDraw();
  pg12.background(colorStroke);
  pg12.endDraw();
}

public void btnColorsRandomizeCallback(GButton source, GEvent event) {
  float diff = 1.0f / PApplet.parseFloat(alpha.length);
  IntList alphaColorList = new IntList();
  for (int i = 0; i < alpha.length; i++) {
    alphaColorList.append(lerpColor(color(PApplet.parseInt(random(255)), PApplet.parseInt(random(255)), PApplet.parseInt(random(255))), color(PApplet.parseInt(random(255)), PApplet.parseInt(random(255)), PApplet.parseInt(random(255))), i*diff));
  }
  alphaColors = alphaColorList.array();
  ca.setAlphaColors(alphaColorList.array());
  pg1.beginDraw();
  pg1.background(alphaColors[selectedColor]);
  pg1.endDraw();
  pg = ca.render();
}

public void btnColorsOkCallback(GButton source, GEvent event) {
  winColors.close();
  winColors = null;
}

public void alphaChooser(GDropList droplist, GEvent event) {
  selectedColor = cdl.getSelectedIndex();
  label3color.setText(alpha[selectedColor]);
  label3color.setTextBold();
  pg1.beginDraw();
  pg1.background(alphaColors[selectedColor]);
  pg1.endDraw();
}

public void createColorsWindow() {
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

public void createSimulatorCAWindow() {
  if (isTS) {
    if (txf2.getText() != "") {
      nh = PApplet.parseFloat(txfh.getText());
      nw = PApplet.parseFloat(txfw.getText());
      println(nh);
      println(nw);
      winSimulatorCA = GWindow.getWindow(this, "SimulatorCA", 280, 0, displayWidth/2 - 280, displayHeight - 60, JAVA2D);
      winSimulatorCA.setActionOnClose(G4P.CLOSE_WINDOW);
      winSimulatorCA.performCloseAction();
      h = nh*sdr1.getValueI();
      w = nw*sdr1.getValueI();
      ca.setScale(sdr1.getValueI());
      ca.initialize(txf2.getText(), PApplet.parseInt(nw), PApplet.parseInt(nh));
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

public void createStatisticsWindow() {
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

public void saveJSON(GButton button, GEvent event) {
  JSONObject newJSON = new JSONObject();
  JSONObject tsJSON = new JSONObject();
  JSONObject caJSON = new JSONObject();
  if (isTS) {
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
  } else {
    G4P.showMessage(this, "There is not a cellular automata, create new or open some one in File", "Cellular automata missed", G4P.ERROR_MESSAGE);
  }

  filePrincipal.setCollapsed(true);
}

public void openJSON(GButton button, GEvent event) {
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

public void generateRandomStringTS(GButton button, GEvent event) {
  gts.setRandomLen(PApplet.parseInt(txf3.getText()));
  txf2.setText(gts.randomInitialString());
  winTSLengthRandom.close();
  winTSLengthRandom = null;
}

public void generateRandomStringCA(GButton button, GEvent event) {
  nh = PApplet.parseFloat(txfh.getText());
  nw = PApplet.parseFloat(txfw.getText());
  ca.initialize(txf2.getText(), PApplet.parseInt(nw), PApplet.parseInt(nh));
  txf2.setText(ca.randomInitialString());
}

public void btnZoomCallback(GButton button, GEvent event) { 
  zoom = PApplet.parseFloat(button.tag)/100;
  zoomPanel.setCollapsed(true);
}

public void createGUI() {
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
  sdr1.setRotation(0.0f, GControlMode.CENTER);
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
  
  GButton btnOpenAbout = new GButton(this, 235, 0, 45, 20);
  btnOpenAbout.setText("About");
  btnOpenAbout.addEventHandler(this, "btnAboutCallback");
  btnOpenAbout.setTextAlign(GAlign.LEFT,GAlign.CENTER);
}



class CA {
  ArrayList<GPointsArray> pointsFrecuency = new ArrayList<GPointsArray>();
  GPointsArray pointsEntropy = new GPointsArray();
  PGraphics pg;
  String[] cells;     // An array of 0s and 1s 
  int generation = 0;  // How many generations?
  int scl = 1;         // How many pixels wide/high is each cell?
  String init;
  String[] e;
  int[] ce;
  StringList all = new StringList();
  StringList allTS = new StringList();
  int h;
  int evolutions = 0;
  boolean showStroke = false;
  String imgPath = "";
  String[] ets;
  StringList rules = new StringList();
  ArrayList<String[]> rulesList = new ArrayList<String[]>();
  int colorStroke = color(32);
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

  public void setETS(String[] _ets) {
    ets = _ets;
  }

  public void setPGraphics(PGraphics _pg) {
    pg = _pg;
  }

  public void setOnlyTS(boolean _ots) {
    onlyTS = _ots;
  }

  public String[] getAlpha() {
    return e;
  }

  public int[] getAlphaColors() {
    return ce;
  }

  public void setAlphaColors(int[] _ce) {
    ce = _ce;
    makeSigmaColorHash();
  }

  public String executeRules (String strp, String a) {
    //rules.shuffle();
    for (String[] rule : rulesList) {
      if (match(strp, rule[0]) != null) {
        return rule[1];
      }
    }
    return a;
  }

  public String randomInitialString() {
    String randomInit = "";
    for (int i = 0; i < cells.length; i++) {
      randomInit = randomInit + e[PApplet.parseInt(random(e.length))];
    }
    return randomInit;
  }

  public void initialize(String _init, int _w, int _h) {
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

  public void generatePoints() {
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
      float h = 0.0f;
      for (int k = 0; k < e.length; k++) {
        GPointsArray gpf = pointsFrecuency.get(k);
        if (tally.hasKey(e[k])) {
          gpf.add(j + 1, tally.get(e[k]));
          if (!e[k].equals("N")) {
            float p;
            if (tally.hasKey("N"))
              p = PApplet.parseFloat(tally.get(e[k]))/PApplet.parseFloat(cells.length - tally.get("N"));
            else
              p = PApplet.parseFloat(tally.get(e[k]))/PApplet.parseFloat(cells.length);
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

  public ArrayList<GPointsArray> getGPointsFrecuency() {
    return pointsFrecuency;
  }

  public GPointsArray getGPointsEntropy() {
    return pointsEntropy;
  }

  // The process of creating the new generation
  public void generate() {
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

  public PGraphics getPG() {
    return pg;
  }
  
  public int getEvolutions() {
    return evolutions;
  }

  public void setScale(int _scl) {
    scl = _scl;
  }

  public void setShowStroke(boolean _show) {
    showStroke = _show;
  }

  public void setImgPath(String _path) {
    imgPath = _path;
  }

  public void saveEvolutions() {
    saveStrings("frame-" + frameCount + ".txt", all.array());
  }

  public void setColorStroke(int _cs) {
    colorStroke = _cs;
  }

  public int getColorStroke() {
    return colorStroke;
  }

  public void makeSigmaColorHash() {
    for (int j = 0; j < e.length; j++) {
      sigmaColor.put(e[j], ce[j]);
    }
  }

  public StringList getAllStrings() {
    StringList toAll = new StringList();
    if (onlyTS) toAll = allTS;
    else toAll = all;
    return toAll;
  }

  public PGraphics render() {
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

  public void reset() {
    pointsFrecuency = new ArrayList<GPointsArray>();
    pointsEntropy = new GPointsArray();
    pg = null;
    generation = 0;  // How many generations?
    all = new StringList();
  }

  public boolean finished() {
    if (generation > h) {
      return true;
    } else {
      return false;
    }
  }
}
class GTS {
  String[][] rules;
  int p, l;
  String actual;
  int[] cr = {0xff77E6D9, 0xffE58860};
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

  public void setMode(int _m) {
    m = _m;
  }

  public void setRandomLen(int _rlen) {
    randomLen = _rlen;
  }

  public StringList getCARules() {
    return caRules;
  }

  public StringList getCAAlpha() {
    return eca;
  }

  public StringList getRules() {
    return productionRules;
  }

  public String randomInitialString() {
    String randomInit = "";
    for (int i = 0; i < randomLen; i++) {
      randomInit = randomInit + e[PApplet.parseInt(random(e.length))];
    }
    return randomInit;
  }

  public void setRules(StringList _productionRules) {
    productionRules = _productionRules;
  }

  public void initRules() {
    for (int i = 0; i < p; i++) {
      for (int j = 0; j < l; j++) {
        rules[i][j] = "";
      }
    }
  }

  public void setScale(int _s) {
    scl = _s;
  }

  public void initColors() {
    float n = parseFloat(l) - 2.0f;
    cr[0] = color(255, 255, 255);
    cr[cr.length - 1] = color(0, 0, 0);
    if (n > 0) {
      float a = 255.0f/(n + 1.0f);
      for (int i = 1; i < cr.length - 1; i++) {
        cr[i] = color(PApplet.parseInt(i*a), PApplet.parseInt(i*a), PApplet.parseInt(i*a));
      }
    }
  }

  public String[] getSymbols() {
    return e;
  }

  public void setSymbols(String[] _e) {
    e = _e;
    l = e.length;
    rules = new String[p][l];
    initRules();
  }

  public void setAppendat(int i, int j, String a) {
    rules[i][j] = a;
  }

  public void printRules() {
    for (int i = 0; i < p; i++) {
      printArray(rules[i]);
    }
  }

  public void setInitialString(String init) {
    actual = init;
  }

  public String getActualString() {
    return actual;
  }

  public int getMode() {
    return m;
  }

  public void generate() {
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

  public float entropy() {
    float n = 0.0f;
    float pr = 0.0f;
    float en = 0.0f;
    for (int j = 0; j < e.length; j++) {
      for (int i = 0; i < actual.length(); i++) {
        if (str(actual.charAt(i)).equals(e[j])) {
          n = n + 1.0f;
        }
      }
      pr = n/parseFloat(actual.length());
      en = en + log(pow(1/pr, pr))/log(l);
      n = 0.0f;
    }
    return en;
  }

  public boolean halt() {
    if (actual.length() < 1 || y*scl > height) {
      return true;
    }
    return false;
  }

  public int getSteps() {
    return y;
  }

  public int[] getColors() {
    return cr;
  }

  public String getRulesString() {
    String ru = "" + p + "-";
    for (int i = 0; i < p; i++) {
      for (int j = 0; j < l; j++) {
        ru = ru + "(" + i + "," + e[j] +")->" + rules[i][j] + ",";
      }
    }
    return ru;
  }

  public void convertToCA() {
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
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "gts2ca" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
