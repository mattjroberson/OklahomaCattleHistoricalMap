import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import http.requests.*; 
import java.util.Collections; 
import java.util.Map; 
import java.util.Map.Entry; 
import java.util.LinkedHashMap; 
import java.util.List; 
import java.util.Arrays; 
import java.io.FileNotFoundException; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class oklahoma_map extends PApplet {









int iter = 0;
int maxIter;
boolean goingUp = true;

PImage stateImage;
PImage titleImage;
County currentPressed;

UserInterface userInterface;
Chart stateChart;

PFont yearFont;
int backgroundColor;

ArrayList<Float> stateDairyData, stateBeefData, stateComparedData;

ArrayList<County> countyList;
ArrayList<Story> storyList;

enum ChartType { KEY, TOTALS, PERCENTAGE, STORY };

ChartType chartType;
ChartType lastChartType;

boolean ctrlPressed;

public void setup() {
  userInterface = new UserInterface();
  
  storyList = new ArrayList<Story>();
  
  yearFont = createFont("Arial Bold", 48);  
  backgroundColor = unhex("D9D1BA");
  
  stateImage = loadImage("oklahoma_blank.png");
  titleImage = loadImage("title.png");
    
  setChartType(ChartType.PERCENTAGE);
  countyList = getCounties();
  
  maxIter = countyList.get(0).colorValues.size()-1;
    
  loadStateValues(countyList);
  stateChart = new Chart("STATE TOTALS", stateDairyData, stateBeefData, stateComparedData);
   
  currentPressed = null;
  ctrlPressed = false;
    
  
    
  frameRate(30);
  
}

public void setChartType(ChartType newType){
  if(newType != ChartType.KEY && newType != ChartType.STORY){
    lastChartType = newType;
  }
  
  chartType = newType;
}

public void setToLastChartType(){
  chartType = lastChartType;
}

public void loadStateValues(final ArrayList<County> counties){ 
  
  stateDairyData = new ArrayList(counties.get(0).milkData);
  stateBeefData = new ArrayList(counties.get(0).beefData);
  stateComparedData = new ArrayList(counties.get(0).comparedData);
  
  for(int i = 1; i < counties.size(); i++){
    for(int x = 0; x < stateDairyData.size(); x++){
      stateDairyData.set(x, stateDairyData.get(x) + counties.get(i).milkData.get(x));
      stateBeefData.set(x, stateBeefData.get(x) + counties.get(i).beefData.get(x));
      stateComparedData.set(x, stateComparedData.get(x) + counties.get(i).comparedData.get(x));
    } 
  }
  
  for(int i = 0; i < stateComparedData.size(); i++){
    stateComparedData.set(i, stateComparedData.get(i) / counties.size());
  }
}

public void keyPressed(){
  if(key == CODED){
    if(keyCode == LEFT && iter > 0){
      userInterface.pauseState();
      shift(-1);
    }
    else if(keyCode == RIGHT && iter < maxIter){
      userInterface.pauseState();
      shift(1);
    }
    else if(keyCode == CONTROL){
      ctrlPressed = true;
    }
  }
  else if(key == ' '){
    userInterface.toggleState();
  }
}

public void keyReleased(){
  if(key == CODED){
    if(keyCode == CONTROL){
      ctrlPressed = false;
    }
  }
}

public void shift(int dir){
  if(ctrlPressed) 
    iter += dir;
  else{
    int i = countyList.get(0).yearIter + dir;
    i = (int) (i * Math.pow(2, 4));
    
    iter = i;
  }
}

public void mouseWheel(MouseEvent event) {
  float e = event.getCount();
  userInterface.pauseState();
  if(0 <= iter + e && maxIter >= iter + e) iter += e;
}

public void mousePressed(){  
  //Check if the user interface is pressed
  if(userInterface.mousePressed() == true) return;
  
  //Check if chart was pressed
  Chart chartPressedCheck = (currentPressed != null) ? currentPressed.chart : stateChart;
  if(chartPressedCheck.mousePressed() == true) return;
  
  //Check every county
  for(County county : countyList){
    //If the county was clicked on
    if(county.mousePressed()){
      //Deselect the old selected county
      if(currentPressed != null && currentPressed != county){
        currentPressed.stopPressed();
      }
      //Reset the chart type to a graph type if its not already
      if(chartType == ChartType.KEY || chartType == ChartType.STORY) {
          setToLastChartType();
      }
      
      //If the county was deselected set current to null, aka state
      currentPressed = county.clicked == false ? null : county;
      
      //Refresh the chart
      if(currentPressed != null){
        currentPressed.chart.refresh();
      }else{
        stateChart.refresh();
      }
      
      //Deselect all the stories
      for(Story story : storyList) story.stopClicked();
      
      break;
    }
    else{
      Story pressedStory = county.storyPressed();
      //If the county returned a story that was pressed
      if(pressedStory != null){
        
        //Deselect the old selected county
        if(currentPressed != null && currentPressed != county){
          currentPressed.stopPressed();
        }
        
        //Deselect all the other stories
        for(Story checkStory : storyList){
          if(checkStory.equals(pressedStory) == false) checkStory.stopClicked();
        }
        
        //Select the county attached to the story, and set the story type
        county.clicked = true;
        currentPressed = county;
        chartType = ChartType.STORY;
        
        //Refresh the chart
        county.chart.refresh();
        
        break;
      }
    }
  }
}

public void mouseReleased(){
  if(currentPressed == null) stateChart.mouseReleased();
  else currentPressed.chart.mouseReleased();
  userInterface.mouseReleased();
}

public void draw(){
  //Render the background components
  background(backgroundColor);
  
  image(stateImage, 0, 100);
  image(titleImage, 0, 15);
  
  //Render the counties
  for(County county : countyList){ //<>// //<>//
    county.render();
  }
  
  //Render the user interface
  userInterface.render(iter, maxIter);

  //Render the current year (staggered for shadow)
  textFont(yearFont);
  fill(125);
  text((1990+countyList.get(0).yearIter),1112,798);
  fill(58);
  text((1990+countyList.get(0).yearIter),1115,795);

  //Render the currently selected chart box
  if(currentPressed == null) stateChart.render();
  else currentPressed.chart.render();
       
  //Render any stories
  for(Story story : storyList) story.render();
        
  //Iterate over time if playing
  if(userInterface.isPlaying == true){
    iterateTime(userInterface.goingForward);
  } 
}

//Iterate over time
public void iterateTime(boolean goingForward){
  if(goingForward == true) {
    //If going forward & is max, pause 
    if(iter == maxIter) {
      userInterface.pauseState(); return;
    }
    iter++;
 }
  else{
    //If going backward & is min, pause
    if(iter == 0) {
      userInterface.pauseState(); return;
    }
    iter--;
  }  
}

//Square mouse bounds function shared by all classes
public boolean mouseBounds(float x, float y, float w, float h){
  if(mouseX >= x && mouseX <= x + w && mouseY <= y + h && mouseY >= y) return true;
  else return false;
}
class Chart {

  float offsetX;
  float offsetY;
  float chartWidth;
  float chartHeight;
  
  ArrayList<ChartTab> tabList;
  
  ChartTab totalTab;
  ChartTab percentTab;
  ChartTab keyTab;
  ArrayList<ChartTab> storyTabs;
  
  float tabWidth;
  
  County county;
  
  ArrayList<Float> milkData, beefData, comparedData;
  String name;
  
  PFont countyFont;
  int countyFontSize;
  
  int red, blue, lineColor;
  
  ArrayList<Story> chartStories;
  PImage keyGraphic;
  
  boolean chartPressed;
  float chartScale;
  
  String sourceLink;
  int linkHeight;
  
  public Chart(String name, ArrayList<Float> milkData, ArrayList<Float> beefData, ArrayList<Float> comparedData){
    this.name = name;
    this.milkData = milkData;
    this.beefData = beefData;
    this.comparedData = comparedData;
    this.county = null;
   
    chartStories = new ArrayList<Story>();

    init();
    refresh();
  }

  public Chart(County county) {   
    this.name = county.name;
    this.milkData = county.milkData;
    this.comparedData = county.comparedData;
    this.beefData = county.beefData;
    this.chartStories = county.countyStories;
    this.county = county;
    
    init();
  }
  
  private void init(){
    chartWidth = 380;
    chartHeight = 290;

    offsetX = 85;
    offsetY = 275;
    
    storyTabs = new ArrayList<ChartTab>();
    tabList = new ArrayList<ChartTab>();
    tabWidth = 30;
    
    float tabTotalX = offsetX - tabWidth + 1;
    float tabTotalYBot = offsetY + 71;
    float tabTotalYMid = offsetY + 71 + 31;
    float tabTotalYTop = offsetY;
    
    totalTab = new ChartTab(ChartType.TOTALS, tabTotalX, new float[]{tabTotalYTop, tabTotalYMid, tabTotalYBot}, tabWidth, 49);
    tabList.add(totalTab);

    float tabPercentX = offsetX - tabWidth + 1;
    float tabPercentYBot = offsetY + 50;
    float tabPercentYMid = offsetY + 31;
    float tabPercentYTop = offsetY;
    
    percentTab = new ChartTab(ChartType.PERCENTAGE, tabPercentX, new float[]{tabPercentYTop, tabPercentYMid, tabPercentYBot}, tabWidth, 70);
    tabList.add(percentTab);

    float tabKeyX = offsetX - tabWidth + 1;    
    float tabKeyYBot = offsetY + 50 + 71;
    float tabKeyYTop = offsetY;

    tabKeyYBot += (31 * chartStories.size());

    keyTab = new ChartTab(ChartType.KEY, tabKeyX, new float[]{tabKeyYTop, 0, tabKeyYBot}, tabWidth, 30);
    tabList.add(keyTab);
    
    float mid2Difference = 31;
    for(int i = 0; i < chartStories.size(); i++){
      float tabStoryX = offsetX - tabWidth + 1;
      float tabStoryYBot = offsetY + 50 + 71 + (31 * i);
      float tabStoryYMid = offsetY + 50 + 71 + 31 + (31 * i);
      float tabStoryYMid2 = offsetY + 50 + 71 + (31 * i) + mid2Difference;
      float tabStoryYTop = offsetY;
      
      ChartTab storyTab = new ChartTab(chartStories.get(i), tabStoryX, new float[]{tabStoryYTop, tabStoryYMid, tabStoryYMid2, tabStoryYBot}, tabWidth, 30);
      storyTabs.add(storyTab);
      tabList.add(storyTab);
      
      mid2Difference -= 31;
    }
    
    chartPressed = false;
    
    countyFontSize = 36;
    countyFont = createFont("Arial Bold", countyFontSize);
    
    red = color(220, 0, 0);
    blue = color(0, 0, 220);
    lineColor = color(150);
    
    keyGraphic = loadImage("key_graphic.png");
    
    chartScale = getChartScale(.1f);
    
    sourceLink = "https://quickstats.nass.usda.gov/";
    linkHeight = 50;
  }

  private float getChartScale(float margin){
    float max = 0;
    
    for(float value : beefData){
      if(value > max) max = value;
    }
    
    return max * ( 1 + margin );
  }
  public void plotTotalChart(ArrayList<Float> data, int col) {
    drawMarkerLines(false);
    
    stroke(col);
    
    float lineWidth = chartWidth / (data.size()-1);
    
    for (int i=0; i<data.size()-1; i++) {
      if (data.get(i) == -1) continue;
      float y1 = map(data.get(i), 0f, chartScale, chartHeight, 0f);
      float y2 = map(data.get(i+1), 0f, chartScale, chartHeight, 0f);

      line(offsetX+i*lineWidth, offsetY+y1, offsetX+(i+1)*lineWidth, offsetY+y2);
    }
  }

  public void plotPercentageChart(ArrayList<Float> data) {
    drawMarkerLines(true);
    
    float lineWidth = chartWidth / (data.size()-1);
    
    for (int i=0; i<data.size()-1; i++) {

      float dataPoint1 = data.get(i);
      float dataPoint2 = data.get(i+1);
      float halfPoint = .5f;

      if (dataPoint1 == -1) continue;

      //Line that crosses the .5 line
      if (dataPoint1 < halfPoint && dataPoint2 > halfPoint) {        

        float halfWidth = lineWidth * ((halfPoint - dataPoint1) / (dataPoint2 - dataPoint1));
        float halfX = (i * lineWidth) + halfWidth;
        float halfY = map(halfPoint, 0, 1, 0, chartHeight);

        float x1 = i * lineWidth;
        float y1 = map(dataPoint1, 0, 1, 0, chartHeight);
        float x2 = halfX;
        float y2 = halfY;

        plotLine(x1, y1, x2, y2, blue);

        x1 = halfX;
        y1 = halfY;
        x2 = (i + 1)*lineWidth;
        y2 = map(dataPoint2, 0, 1, 0, chartHeight);

        plotLine(x1, y1, x2, y2, red);
      } else if (dataPoint1 > halfPoint && dataPoint2 < halfPoint) {        

        float halfWidth = lineWidth * ((halfPoint - dataPoint1) / (dataPoint2 - dataPoint1));
        float halfX = (i * lineWidth) + halfWidth;
        float halfY = map(halfPoint, 0, 1, 0, chartHeight);

        float x1 = i * lineWidth;
        float y1 = map(dataPoint1, 0, 1, 0, chartHeight);
        float x2 = halfX;
        float y2 = halfY;

        plotLine(x1, y1, x2, y2, red);

        x1 = halfX;
        y1 = halfY;
        x2 = (i + 1) * lineWidth;
        y2 = map(dataPoint2, 0, 1, 0, chartHeight);

        plotLine(x1, y1, x2, y2, blue);
      }
      //Line that doesnt cross the .5 line
      else {
        float x1 = i * lineWidth;
        float y1 = map(dataPoint1, 0, 1, 0, chartHeight);
        float x2 = (i + 1) * lineWidth;
        float y2 = map(dataPoint2, 0, 1, 0, chartHeight);

        int col;

        if (dataPoint1 == halfPoint) {
          if (dataPoint2 < halfPoint) {
            col = blue;
          } else {
            col = red;
          }
        } else {
          col = dataPoint1 < halfPoint ? blue : red;
        }

        plotLine(x1, y1, x2, y2, col);
      }
    }
  }

  public void plotLine(float x1, float y1, float x2, float y2, int col) {
    stroke(col);
    line(offsetX+x1, offsetY+y1, offsetX+x2, offsetY+y2);
  }
  
  public void drawMarkerLines(boolean isPercent){
    strokeWeight(1);
    
    drawSingleMarker(.17f, isPercent); 
    drawSingleMarker(.34f, isPercent);

    if(isPercent) strokeWeight(3);
    drawSingleMarker(.5f, isPercent);
    if(isPercent) strokeWeight(1);
    
    drawSingleMarker(.67f, isPercent);
    drawSingleMarker(.84f, isPercent);
    
    strokeWeight(3);
  }
  
  public void drawSingleMarker(float val, boolean isPercent){
    float yValue = map(val, 0, 1, 0, chartHeight);
    plotLine(0, yValue, chartWidth, yValue, lineColor);
    
    int shiftY = isPercent == true ? 4 : 2;
    
    Object textVal;
    
    if(isPercent == true){
      textVal = val;
    }
    else{
     float calcVal = (1 - val) * chartScale;
     textVal = (int)(Math.round( calcVal / 100.0f) * 100);
    }
    
    textSize(12);
    textAlign(RIGHT);
    fill(lineColor);
    text(String.valueOf(textVal), offsetX + chartWidth - 2, offsetY + chartHeight - yValue - shiftY);
    textAlign(LEFT);
    text(String.valueOf(textVal), offsetX + 4, offsetY + chartHeight - yValue - shiftY);
  }
  
  public boolean mousePressed(){
    //Check all tabs
    for(ChartTab tab : tabList){
      //If tab pressed
      if(tab.mousePressed() == true){
        //If the chart had been a story, deselect all stories
        if(chartType == ChartType.STORY) {
          for(Story story: chartStories) story.stopClicked();
        }
        //If the tab is a story, select it
        if(tab.type == ChartType.STORY) {
          tab.story.select();
        }
        
        //Set the chart type and refresh
        setChartType(tab.type);
        refresh();
        
        return true;
      }
    }
    
    //If clicked on the chart surface
    if(!(chartType == ChartType.KEY)  && !(chartType == ChartType.STORY) && mouseBounds(offsetX, offsetY, chartWidth, chartHeight) == true){
      userInterface.pauseState();
      chartPressed = true;
      
      iter = (int)map(mouseX, offsetX, offsetX + chartWidth, 0, maxIter);
      return true;
    }
    
    //If pressed a link
    if(mouseBounds(offsetX, offsetY+chartHeight - linkHeight, chartWidth, chartHeight) == true){
      if(chartType == ChartType.KEY) {
        link(sourceLink);
      }
      else if(chartType == ChartType.STORY){
        for(Story story : storyList){
          if(story.clicked) {
            link(story.link);
          }
        } 
      }
    }
    return false;
  }
  
  public void mouseReleased(){
    chartPressed = false;
  }
 
  private void refresh(){
    clearSelectedTabs();
    
    if(chartType == ChartType.TOTALS){
      totalTab.select();
      totalTab.setY("top");
      percentTab.setY("bottom");
      setStoryYs("bottom");
      keyTab.setY("bottom");
    }
    else if(chartType == ChartType.PERCENTAGE){
      percentTab.select();
      totalTab.setY("bottom");
      percentTab.setY("top");
      setStoryYs("bottom");
      keyTab.setY("bottom");
    }
    else if(chartType == ChartType.KEY){
      keyTab.select();
      totalTab.setY("middle");
      percentTab.setY("middle");
      setStoryYs("middle");
      keyTab.setY("top");
    }
    else if(chartType == ChartType.STORY){
      setStoryTabsSelected();
      totalTab.setY("middle");
      percentTab.setY("middle");
      setStoryYs("top");
      keyTab.setY("bottom");
    }

  }
  
  private void clearSelectedTabs(){
    for(ChartTab tab : tabList){
      if(tab.type != chartType) {
        tab.clicked = false;
      }
      else if(chartType == ChartType.STORY) {
        if(tab.story.clicked == false) {
          tab.clicked = false;
        }
      }
    }
  }
  
  private void setStoryYs(String position){
    //If setting a story two the top
    if(position.equals("top")){
      for(ChartTab tab : storyTabs){
        //If the story is the selected story set it to the top, otherwise, set it to the middle
        position = (tab.story.clicked == true) ? "top" : "middle 2";
        tab.setY(position);
      }
    }
    //Otherwise just set the position
    else{
      for(ChartTab tab : storyTabs){
        tab.setY(position);
      }
    }
  }

  //Sets any story tabs image to selected as necessary
  private void setStoryTabsSelected(){
    for(ChartTab tab : storyTabs){ 
      if(tab.story.clicked == true) tab.select();
    }
  }
  public void render() {
    //Check if any tabs are hovered
    for(ChartTab tab : tabList) tab.checkHovered();
    
    //Handle the location of the line when pressing on the chart
    if(chartPressed){
      float i = mouseX;
      
      i = userInterface.capValue((int)i, (int)offsetX, (int)(offsetX + chartWidth));
      iter = (int)map(i, offsetX, offsetX + chartWidth, 0, maxIter);
    }
    
    //Draw all chart tabs
    for(ChartTab tab : tabList) tab.render();
        
    //The the background rectangle
    if((chartType == ChartType.KEY)) {
      fill(64);
      stroke(255);
    }
    else {
      fill(200);
      stroke(0);
    }
    
    strokeWeight(4);
    rect(offsetX-4, offsetY-4, chartWidth+8, chartHeight+8);
    strokeWeight(3);

    //Draw the graph
    noFill();
    if(chartType == ChartType.PERCENTAGE){
      plotPercentageChart(comparedData);
    }
    else if(chartType == ChartType.TOTALS){
      plotTotalChart(milkData, blue);
      plotTotalChart(beefData, red);
    }
    else if(chartType == ChartType.KEY){
      image(keyGraphic, offsetX-2, offsetY-2);
    }
    else if(chartType == ChartType.STORY){
      drawStory(offsetX-2, offsetY-2);
    }
    
    //Draw the time line
    if(!(chartType == ChartType.KEY) && !(chartType == ChartType.STORY)){
      stroke(255);
      float linePos = map(iter, 0, maxIter, 0, chartWidth);
      line(offsetX+linePos, offsetY, offsetX+linePos, offsetY+chartHeight);
    }

    //Draw the county name
    textAlign(LEFT);
    textFont(countyFont);

    fill(125);
    text(name, offsetX - 3, offsetY + chartHeight + countyFontSize + 8);

    fill(58);
    text(name, offsetX, offsetY + chartHeight + countyFontSize + 5);
  }
  
  //Draw the story graphic
  private void drawStory(float x, float y){
    for(Story story : chartStories){
      if(story.clicked == true) {
        story.drawStory(x, y);
        return;
      }
    }
  }
}


class County {
  String name;
  String abrev;
  int x;
  int y;
  
  float textX;
  float textY;
  float textSize;
  float textWidth;
  
  int textOffsetX = 0;
  int textOffsetY = 0;
  
  float linePos;
  
  boolean hovered;
  boolean clicked;
  
  Map<String, String> params;

  ArrayList<Float> milkData, beefData, comparedData;
  ArrayList<Integer> yearData;
  ArrayList<Integer> colorValues;

  int red, blue;

  ArrayList<Story> countyStories;
  Chart chart;

  PImage countyImage;
  int firstYear;
  
  PFont regFont;
  PFont boldFont;
  
  int numDivs;
  int divScale;
  int yearIter;

  String prefix;

  County(String name, String abrev, Story[] storyList, int x, int y) {
    this.name = name;
    this.abrev = abrev;
    this.x = x;
    this.y = y;
    
    countyStories = new ArrayList<Story>();
    
    for(int i = 0; i < storyList.length; i++){
      countyStories.add(storyList[i]);
    }
    
    firstYear = 1990;

    yearData = new ArrayList<Integer>();
    comparedData = new ArrayList<Float>();
    params = new LinkedHashMap<String, String>();

    countyImage = loadImage(name+".png");
    hovered = false;
    clicked = false;
    textSize = 15;
    
    regFont = createFont("Arial", textSize);
    boldFont = createFont("Arial Bold", textSize*1.25f);

    prefix = "https://quickstats.nass.usda.gov/api/api_GET/?";

    params.put("key", "43D04181-32AD-3085-B721-8B63F6E6DF2F");
    params.put("commodity_desc", "CATTLE");
    params.put("statisticcat_desc", "INVENTORY");
    params.put("short_desc", "CATTLE,%20COWS,%20MILK%20-%20INVENTORY");
    params.put("domain_desc", "TOTAL");
    params.put("state_alpha", "OK");
    params.put("agg_level_desc", "COUNTY");
    params.put("county_name", name);
    params.put("year__GE", String.valueOf(firstYear));
    params.put("source_desc", "SURVEY");
    params.put("format", "JSON");
    params.put("freq_desc", "POINT%20IN%20TIME");

    
    milkData = this.getValues(true);
    Collections.reverse(milkData);
    Collections.sort(yearData);

    params.replace("short_desc", "CATTLE,%20COWS,%20BEEF%20-%20INVENTORY");

    beefData = this.getValues(false);
        
    Collections.reverse(beefData);
        
    comparedData = CountyData.compareValues(beefData, milkData);
    comparedData = CountyData.resizeValues(comparedData);
    
    numDivs = 4;
    divScale = (int)Math.pow(2, numDivs);

    red = color(255,0,0);
    blue = color(0,0,255);

    colorValues = getColorValues(numDivs);
    
    chart = new Chart(this);

    boolean first = true;
    for(Story story : countyStories) {
      story.attachChart(chart, first);
      first = !first;
    }
  }
  
  County(String name, String abrev,Story[] storyList, int x, int y, int textOffsetX, int textOffsetY){
    this(name, abrev, storyList, x, y);
    
    this.textOffsetX = textOffsetX;
    this.textOffsetY = textOffsetY;
  }
  
  County(String name, String abrev, Story story, int x, int y){
    this(name, abrev, new Story[]{story}, x, y);
  }
  
  County(String name, String abrev, Story story, int x, int y, int textOffsetX, int textOffsetY) {
    this(name, abrev, new Story[]{story}, x, y);
    
    this.textOffsetX = textOffsetX;
    this.textOffsetY = textOffsetY;
  }
  
  County(String name, String abrev, int x, int y){
    this(name, abrev, new Story[]{}, x, y);
  }
  
  County(String name, String abrev, int x, int y, int textOffsetX, int textOffsetY) {
    this(name, abrev, new Story[]{}, x, y, textOffsetX, textOffsetY);
  }
  
  
  public ArrayList<Float> getValues(boolean getYearData) {
    String suffix = getYearData == true ? "-milk-data.txt" : "-beef-data.txt";
    
    File f = new File("countyData/"+name+suffix);
    ArrayList<CountyData> yearList = new ArrayList<CountyData>();
    JSONObject json;

    if (f.exists()) {
      String[] lines = loadStrings(f);
      yearList = CountyData.convertArray(lines);

    } 
    else {
      GetRequest get = new GetRequest(this.getAPIString());
      get.send();
      
      String content = get.getContent();
      json = parseJSONObject(content);
  
      JSONArray jsonList = json.getJSONArray("data");
      yearList = CountyData.convertJSON(jsonList); 
    }

    Map<Integer, Float> dataTable = new LinkedHashMap<Integer, Float>();
      
    for (int i = 0; i < yearList.size(); i++) {
      int year = yearList.get(i).year;

      String rawValue = yearList.get(i).value;
      Float value;

      try {
        value = Float.valueOf(rawValue.replace(",", ""));
      }
      catch(NumberFormatException e) {
        value = -1f;
      }

      //Fill in any empty years at the beginning of the list
      if (dataTable.size() == 0) {
        int curYear = 2019;
        while (curYear > year) {
          dataTable.put(curYear, -1f);

          if (getYearData == true)yearData.add(curYear);

          curYear--;
        }
      }
      //Fill in any empty years in the middle of the list
      else {
        int curYear = year + 1;

        while (dataTable.containsKey(curYear) == false) {
          dataTable.put(curYear, -1f);

          if (getYearData == true)yearData.add(curYear);

          curYear += 1;
        }
      }

      dataTable.put(year, value);

      if (getYearData == true && yearData.contains(year) == false)yearData.add(year);
    }

    //Fill in any empty years at the end of the list
    if (dataTable.size() != (2020-firstYear)) {
      int curYear = 2020 - dataTable.size() - 1;
      while (curYear >= firstYear) {
        dataTable.put(curYear, -1f);

        if (getYearData == true)yearData.add(curYear);

        curYear--;
      }
    }

    if(f.exists() == false) writeToFile(dataTable, f);
    
    return new ArrayList<Float>(dataTable.values());
  }
  
  public void writeToFile(Map<Integer, Float> data, File f){
    try {
        PrintWriter p = new PrintWriter(f);
        for(Entry<Integer, Float> entry : data.entrySet()){
          p.println(entry.getKey()+":"+entry.getValue());
        }
        p.close();
      }
      catch(FileNotFoundException e) {
      }
  }

  public String getAPIString() {
    String url = prefix;
    for (Entry<String, String> entry : params.entrySet()) {
      url += entry.getKey() + "=" + entry.getValue().replaceAll(" ", "%20") + "&";
    }

    return url;
  }  

  public ArrayList<Integer> getColorValues(int divCount){
    ArrayList<Integer> colorVals = new ArrayList<Integer>();
    
    for(int i = 0; i < yearData.size(); i++){
      colorVals.add(comparedData.get(i) != -1 ? lerpColor(blue, red, comparedData.get(i)) : color(200));
    } 
        
    if(divCount > 0) colorVals = subDivideColors(colorVals, divCount);
    return colorVals;
  }
  
  public ArrayList<Integer> subDivideColors(ArrayList<Integer> vals, int divCount){
    ArrayList<Integer> newVals = new ArrayList<Integer>();

    for(int i = 0; i < vals.size()-1; i++){
      int newVal = lerpColor(vals.get(i), vals.get(i+1), .5f);
      ArrayList<Integer> tempVals = new ArrayList<Integer>();
      
      tempVals.add(vals.get(i));
      tempVals.add(newVal);
      tempVals.add(vals.get(i+1));
      
      if(divCount > 1) newVals = addVals(newVals, subDivideColors(tempVals, divCount-1));
      else newVals = addVals(newVals, tempVals);
    }
    
    return newVals;
  }

  public ArrayList addVals(ArrayList list1, ArrayList list2){
    if(list1.size() == 0) return list2;
    
    for(int i = 1; i < list2.size(); i++){
      list1.add(list2.get(i));
    }
    
    return list1;
  }

  public void render() {  

    textAlign(CENTER);
    
    tint(colorValues.get(iter));
    image(countyImage, x, y);
    noTint();
    
    yearIter = (iter % divScale == 0) ? (int)(iter / divScale) : (int)Math.ceil(iter / divScale); 
    
    textWidth = textWidth(abrev);
    
    textX = x + (countyImage.width / 2) + textOffsetX;
    textY = y + (countyImage.height / 2) + (textSize / 2)  + textOffsetY;
    
    checkMousePos();
    
    text(abrev, textX, textY);
    
    for(Story story : countyStories){
      story.render();
    }
  }
  
  public void checkMousePos(){
    if(mouseX >= textX - (textWidth/2)&& mouseX <= textX + (textWidth/2) && mouseY >= textY - textSize && mouseY <= textY){
      fill(252, 226, 5);
      textFont(boldFont);
      hovered = true;
    }
    else{
      if(clicked == false) {
        fill(255);
        textFont(regFont);
      }
      else {
        fill(252, 226, 5);
        textFont(boldFont);
      }
      hovered = false;
    }
  }
  
  public boolean mousePressed(){
    if(hovered == true) {
      clicked = !clicked;
      return true;
    }
    return false;
  }
  
  //Returns the story that was pressed, and null if none pressed
  public Story storyPressed(){
    for(Story story : countyStories){
      if(story.mousePressed() == true) return story;
    }
    return null;
  }
  
  public void stopPressed(){
    clicked = false;
  }
}
static class CountyData{
  public int year;
  public String value;
  
  CountyData(int y, String v){
    year = y;
    value = v;
  }
  
  public static ArrayList<CountyData> convertJSON(JSONArray jsonArray){
    ArrayList<CountyData> data = new ArrayList<CountyData>();
    
    for(int i = 0; i < jsonArray.size(); i++){
      int y = jsonArray.getJSONObject(i).getInt("year");
      String v = jsonArray.getJSONObject(i).getString("Value");
            
      data.add(new CountyData(y, v));
    }
    return data;
  }
  
  public static ArrayList<CountyData> convertArray(String[] list){
    ArrayList<CountyData> data = new ArrayList<CountyData>();

    for(int i = 0; i < list.length; i++){
      String[] info = list[i].split(":");
      data.add(new CountyData(Integer.valueOf(info[0]), info[1]));
    }
    
    return data;
  }
  
  public static ArrayList<Float> resizeValues(ArrayList<Float> data) {

    float min = data.get(0);
    float max = 0;

    for (int x = 0; x < data.size(); x++) {
      if (data.get(x) < min && data.get(x) != -1) min = data.get(x);
      if (data.get(x) > max) max = data.get(x);
    }

    for (int i = 0; i < data.size(); i++) {
      //IF there is a real value, (not -1), scale it
      if (data.get(i) != -1) {
        data.set(i, map(data.get(i), min, max, 0, 1));
      }
    }
    
    return data;
  }
  
  public static ArrayList<Float> compareValues(ArrayList<Float> data1, ArrayList<Float> data2) {
    ArrayList<Float> finData = new ArrayList<Float>();

    for (int x = 0; x < data1.size(); x++) {      
      //IF either milk or beef data didn't have a value (-1) pass -1
      if (data1.get(x) == -1 || data2.get(x) == -1) {
        finData.add(-1f);
      }
      //ELSE add the difference between the two to the list
      else {
        finData.add(data1.get(x) - data2.get(x));
      }
    }

    return finData;
  }
}
public ArrayList<County> getCounties(){
  //Current maximum stories per county: 2. Tabs need work for more
  
  ArrayList<County> countyList = new ArrayList<County>();
  
  Story comancheStory = new Story("comanche1", "https://dc.library.okstate.edu/digital/collection/Farm/id/3895/rec/1");
  storyList.add(comancheStory);
  
  Story kingfisherStory = new Story("kingfisher1", "https://dc.library.okstate.edu/digital/collection/Farm/id/3951/rec/1");
  Story kingfisherStory2 = new Story("kingfisher2", "https://oklahoman.com/article/3100146/got-cowsbrspan-classhl2small-dairies-sell-out-as-costs-competition-risespan");
  storyList.add(kingfisherStory);
  storyList.add(kingfisherStory2);
  
  Story payneStory = new Story("payne1", "https://dc.library.okstate.edu/digital/collection/Farm/id/3571/rec/1");
  storyList.add(payneStory);
  
  Story lincolnStory = new Story("lincoln1",  "https://dc.library.okstate.edu/digital/collection/Farm/id/3626/rec/11");
  Story lincolnStory2 = new Story("lincoln2", "https://dc.library.okstate.edu/digital/collection/Farm/id/3714/rec/1");
  storyList.add(lincolnStory);
  storyList.add(lincolnStory2);
  
  Story nobleStory = new Story("noble1", "https://dc.library.okstate.edu/digital/collection/Farm/id/3796/rec/4");
  storyList.add(nobleStory);
  
  Story murrayStory = new Story("murray1", "https://oklahoman.com/article/3100146/got-cowsbrspan-classhl2small-dairies-sell-out-as-costs-competition-risespan");
  storyList.add(murrayStory);
  
  countyList.add(new County("ADAIR", "AD", 1233, 305));
  countyList.add(new County("ALFALFA", "AL", 711, 164));
  countyList.add(new County("ATOKA", "AT", 1012, 562));
  countyList.add(new County("BEAVER", "BV", 367, 164));
  countyList.add(new County("BECKHAM", "BK", 505, 416));
  countyList.add(new County("BLAINE", "BL", 699, 308));
  countyList.add(new County("BRYAN", "BR", 984, 652));
  countyList.add(new County("CADDO", "CA", 699, 415));
  countyList.add(new County("CANADIAN", "CN", 739, 386));
  countyList.add(new County("CARTER", "CR", 846, 595));
  countyList.add(new County("CHEROKEE", "CZ", 1165, 305, 5, 0));
  countyList.add(new County("CHOCTAW", "CW", 1077, 652));
  countyList.add(new County("CIMARRON", "CI", 93, 164));
  countyList.add(new County("CLEVELAND", "CL", 831, 446, 15, -10));
  countyList.add(new County("COAL", "CO", 996, 550));
  countyList.add(new County("COMANCHE", "CC", comancheStory, 666, 533));
  countyList.add(new County("COTTON", "CT", 688, 593));
  countyList.add(new County("CREEK", "CG", 978, 308));
  countyList.add(new County("CRAIG", "CK", 1144, 164));
  countyList.add(new County("CUSTER", "CS", 592, 370));
  countyList.add(new County("DELAWARE", "DL", 1201, 218));
  countyList.add(new County("DEWEY", "DW", 592, 307));
  countyList.add(new County("ELLIS", "EL", 505, 235, -10, 0));
  countyList.add(new County("GARFIELD", "GA", 768, 233));
  countyList.add(new County("GARVIN", "GV", 831, 533));
  countyList.add(new County("GRADY", "GD", 771, 448));
  countyList.add(new County("GRANT", "GT", 768, 164));
  countyList.add(new County("GREER", "GE", 514, 488));
  countyList.add(new County("HARMON", "HM", 505, 503, -3, 0));
  countyList.add(new County("HARPER", "HP", 505, 164));
  countyList.add(new County("HASKELL", "HK", 1146, 427, 0, 10));
  countyList.add(new County("HUGHES", "HG", 999, 460));
  countyList.add(new County("JACKSON", "JA", 523, 535));
  countyList.add(new County("JEFFERSON", "JE", 765, 634, 0, -15));
  countyList.add(new County("JOHNSTON", "JN", 934, 595));
  countyList.add(new County("KAY", "KA", 862, 164));
  countyList.add(new County("KINGFISHER", "KF", new Story[]{kingfisherStory, kingfisherStory2}, 756, 308));
  countyList.add(new County("KIOWA", "KW", 594, 491));
  countyList.add(new County("LATIMER", "LA", 1140, 496));
  countyList.add(new County("LEFLORE", "LE", 1203, 440));
  countyList.add(new County("LINCOLN", "LN", new Story[]{lincolnStory, lincolnStory2}, 907, 350));
  countyList.add(new County("LOGAN", "LG", 831, 308));
  countyList.add(new County("LOVE", "LV", 846, 670, 0, -15));
  countyList.add(new County("MAJOR", "MA", 651, 250));
  countyList.add(new County("MARSHALL", "MR", 930, 652));
  countyList.add(new County("MAYES", "ME", 1144, 247));
  countyList.add(new County("MCINTOSH", "MO", 1069, 412, 0, -5));
  countyList.add(new County("MCCLAIN", "ML", 831, 457, -25, 20));
  countyList.add(new County("MCCURTAIN", "MC", 1192, 590));
  countyList.add(new County("MURRAY", "MU", murrayStory, 877, 571, 10, 0));
  countyList.add(new County("MUSKOGEE", "MK", 1098, 362, 0, -15));
  countyList.add(new County("NOBLE", "NB", nobleStory, 862, 233));
  countyList.add(new County("NOWATA", "NW", 1098, 164));
  countyList.add(new County("OKFUSKEE", "OF", 979, 398));
  countyList.add(new County("OKLAHOMA", "OK", 831, 386));
  countyList.add(new County("OKMULGEE", "OM", 1039, 362, 0, -5));
  countyList.add(new County("OSAGE", "OS", 913, 164, 25, 0));
  countyList.add(new County("OTTAWA", "OT", 1201, 164));
  countyList.add(new County("PONTOTOC", "PN", 936, 515));
  countyList.add(new County("POTTAWATOMIE", "PT", 907, 431, -10, 0));
  countyList.add(new County("PAWNEE", "PW", 919, 241, -10, 10));
  countyList.add(new County("PAYNE", "PA", payneStory, 874, 296, 10, 0));
  countyList.add(new County("PITTSBURG", "PB", 1057, 455));
  countyList.add(new County("PUSHMATAHA", "PM", 1102, 562));
  countyList.add(new County("ROGER MILLS", "RM", 505, 331));
  countyList.add(new County("ROGERS", "RG", 1089, 230));
  countyList.add(new County("SEMINOLE", "SM", 954, 431));
  countyList.add(new County("SEQUOYAH", "SY", 1188, 394, 0, -5));
  countyList.add(new County("STEPHENS", "ST", 763, 563));
  countyList.add(new County("TEXAS", "TX", 231, 164));
  countyList.add(new County("TILLMAN", "TL", 606, 569));
  countyList.add(new County("TULSA",  "TU", 1023, 265, 20, 10));
  countyList.add(new County("WAGONER", "WG", 1089, 310));
  countyList.add(new County("WASHINGTON", "WA", 1066, 164));
  countyList.add(new County("WASHITA", "WT", 592, 430));
  countyList.add(new County("WOODS", "WD", 589, 164, 15, -5));
  countyList.add(new County("WOODWARD", "WW", 561, 202));
  
  return countyList;
}
class Story{
  
  PImage storyImage;
  PImage storyImageHighlight;
  boolean hovered;
  private boolean clicked;
  
  PImage storyChartImage;
  Chart chart;
  
  String name;
  String link;
  
  int x, y, w, h;
  
  public Story(String name, String link, int x, int y){
    this.name = name;
    this.link = link;
    
    storyImage = loadImage("story_image.png");
    storyImageHighlight = loadImage("story_image_highlight.png");
    storyChartImage = loadImage(name + ".png");
        
    this.x = x;
    this.y = y;
    
    w = 25;
    h = 29;
    
    clicked = false;
  }
  
  public Story(String name, String link){
    this(name, link, 0,0);
  }
  
  public void attachChart(Chart chart, boolean first){
    this.chart = chart;
    if(first){
    x += chart.county.x + 1;
    y += chart.county.y + 1;
    }
    else{
      x += chart.county.x + chart.county.countyImage.width - w - 1;
      y += chart.county.y + chart.county.countyImage.height - h - 1;
    }
  }
  
  public boolean mousePressed(){
    if(hovered){
      clicked = true;
      return true;
    }
    else return false;
  }
  
  public void stopClicked(){
    clicked = false;
  }
  
  public void select(){
    clicked = true;
  }
  
  public void drawStory(float x, float y){
    image(storyChartImage, x, y);
  }
  
  public void render(){
    hovered = mouseBounds(x, y, w, h);
    
    if(hovered || clicked){
      image(storyImageHighlight, x, y);
    }
    else{
      image(storyImage, x, y);
    }
  }
}
class ChartTab {
  private PImage tabSelected;
  private PImage tabHighlighted;
  private PImage tabDark;
  private PImage tabCurrent;
  
  private float x;
  private float y;
  private float w;
  private float h;
  private float yTop;
  private float yMid;
  private float yMid2;
  private float yBot;
  
  boolean clicked;
  boolean hovered;
  boolean isStoryTab;
  
  ChartType type;
  Story story;
  
  String name;
      
  public ChartTab(ChartType type, float x, float[] yVals, float w, float h){
    this.type = type;
    name = getNameFromType();
    
    this.x = x;
    this.w = w;
    this.h = h;
    
    yTop = yVals[0];
    yMid = yVals[1];
    
    if(type == ChartType.STORY) {
      yMid2 = yVals[2];
      yBot = yVals[3];
    }
    else {
      yBot = yVals[2];
    }
    
    setYValue();

    clicked = false;
    hovered = false;
    isStoryTab = false;
    
    loadImages();
  }
  
  public ChartTab(Story story, float x, float[] yVals, float w, float h){
    this(ChartType.STORY, x, yVals, w, h);
    this.story = story;
    isStoryTab = true;
  }
  
  private void setYValue(){
     switch (type) {
       case TOTALS:
           if(chartType == ChartType.TOTALS) y = yTop;
           else if(chartType == ChartType.KEY) y = yMid;
           else y = yBot;
           break;
       case PERCENTAGE:
           if(chartType == ChartType.PERCENTAGE) y = yTop;
           else if(chartType == ChartType.KEY) y = yMid;
           else y = yBot;
           break;
       case KEY:
           if(chartType == ChartType.KEY) y = yTop;
           else y = yBot;
           break;
       case STORY:
           if(chartType == ChartType.STORY)  y = yTop;
           else if(chartType == ChartType.KEY) y = yMid;
           else y = yBot;
     }
  }
  
  private String getNameFromType(){
    switch (type) {
      case TOTALS: return "total";
      case PERCENTAGE: return "percent";
      case KEY: return "key";
      case STORY: return "story";
      default: return "na";
    }
  }
  
  private void loadImages(){
    tabSelected = loadImage("tab_" + name + "_selected.png");
    tabHighlighted = loadImage("tab_" + name + "_highlight.png");
    tabDark = loadImage("tab_" + name + "_dark.png");
    tabCurrent = (chartType == type) ? tabSelected : tabDark;
  }
  
  public boolean mousePressed(){
    if(hovered == true){
      clicked = true;
      return true;
    }
    return false;
  }

  public void setY(String position){    
    switch(position) {
      case "top": 
        y = yTop;
        break;
      case "middle":
        y = yMid;
        break;
      case "middle 2":
         y = yMid2;
         break;
      case "bottom":
        y= yBot;
        break;
    }
  }
  
  public void select(){
    tabCurrent = tabSelected;
    clicked = true;
  }
  
  public void checkHovered(){
    if(mouseBounds(x, y, w, h) == true){
      if(!clicked) tabCurrent = tabHighlighted;
      hovered = true;
    }
    else{
      if(!clicked) tabCurrent = tabDark;
      hovered = false;
    }
  }
  
  public void stopClicked(){
    clicked = false;
  }
  
  public void render(){
    image(tabCurrent, x, y);
  }
}
class UserInterface {
  
  PImage playImage;
  PImage playHighlightImage;
  PImage pauseImage;
  PImage pauseHighlightImage;
  PImage stateImage;
  boolean isPlaying;
  int stateX;
  int stateY;
  
  PImage forwardImage;
  PImage forwardHighlightImage;

  PImage reverseImage;
  PImage reverseHighlightImage;
  
  PImage dirImage;
  boolean goingForward;
  int dirX;
  int dirY;
  
  PImage timelineImage;
  PImage curSliderImage;
  PImage sliderImage;
  PImage sliderHighlightImage;
  
  boolean clickingState;
  boolean clickingDir;
  boolean clickingSlider;
  
  int iconSize;
  float sliderPos;
  
  public UserInterface(){
    playImage = loadImage("play.png");
    pauseImage = loadImage("pause.png");
    stateImage = playImage;
    isPlaying = false;
    stateX = 190;
    stateY = 750; 
    
    playHighlightImage = loadImage("play_highlight.png");
    pauseHighlightImage = loadImage("pause_highlight.png");
        
    forwardImage = loadImage("forward.png");
    reverseImage = loadImage("reverse.png");
    dirImage = forwardImage;
    goingForward = true;
    dirX = 265;
    dirY = 750;
    
    forwardHighlightImage = loadImage("forward_highlight.png");
    reverseHighlightImage = loadImage("reverse_highlight.png");
    
    timelineImage = loadImage("timeline.png");
    sliderImage = loadImage("slider.png");
    sliderHighlightImage = loadImage("slider_highlight.png");
    curSliderImage = sliderImage;
    
    clickingState = false;
    clickingDir = false;
    clickingSlider = false;
    
    iconSize = 60;
  }
  
  public void render(int drawIter, int maxIter){
    if(clickingSlider){
      sliderPos = mouseX;
      sliderPos = capValue((int)sliderPos, 340, 1025);
      
      iter = (int)map(sliderPos, 340, 1025, 0, maxIter);
    }
    else {
      sliderPos = map(drawIter, 0, maxIter, 340, 1025);
    }
    image(stateImage, stateX, stateY);
    image(dirImage, dirX, dirY);
    
    image(timelineImage, 340, 775);
    image(curSliderImage, sliderPos, 755);
    
  }
  
  public boolean mousePressed(){
    if(mouseBounds(stateX, stateY, iconSize, iconSize)){
      startStateClicked();
      return true;
    }
    if(mouseBounds(dirX, dirY, iconSize, iconSize)){
      startDirClicked();
      return true;
    }
    if(mouseBounds((int)sliderPos, 755, 25, 50)){
      startSliderClicked();
      return true;
    }
    if(mouseBounds(340, 755, 710, 50)){
      timelineClicked();
      return true;
    }
    
    return false;
  }
  
  public void mouseReleased(){
    if(clickingState == true){
      clickingState = false;
      stateClicked();
    }
    else if(clickingDir == true){
      clickingDir = false;
      dirClicked();
    }
    else if(clickingSlider == true){
      clickingSlider = false;
      curSliderImage = sliderImage;
    }
  }
  
  private void stateClicked(){
    toggleState();
  }
  
  private void startStateClicked(){
    clickingState = true;
    stateImage = (isPlaying == true) ? pauseHighlightImage : playHighlightImage;
  }
  
  private void dirClicked(){
    goingForward = !goingForward;
    dirImage = (goingForward == true) ? forwardImage : reverseImage;
  }
  
  private void startDirClicked(){
    clickingDir = true;
    dirImage = (goingForward == true) ? forwardHighlightImage : reverseHighlightImage;
  }
  
  private void startSliderClicked(){
    clickingSlider = true;
    curSliderImage = sliderHighlightImage;
    pauseState();
  }
  
  private void timelineClicked(){
    pauseState();
    
    sliderPos = mouseX;
    sliderPos = capValue((int)sliderPos, 340, 1025);
      
    iter = (int)map(sliderPos, 340, 1025, 0, maxIter);
  }
  
  public void pauseState(){
    isPlaying = false;
    stateImage = playImage;
  }
  
  public void toggleState(){
    isPlaying = !isPlaying;
    stateImage = (isPlaying == true) ? pauseImage : playImage;
    
    if(isPlaying == true) handleResets();
  }
  
  private void handleResets(){
    if(goingForward == true){
      if(iter == maxIter) iter = 0;
    }
    else{
      if(iter == 0) iter = maxIter;
    }
  }
  
  private int capValue(int val, int min, int max){
    if(val < min) return min;
    else if(val > max) return max;
    else return val;
  }
}
  public void settings() {  size(1380, 825);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "oklahoma_map" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
