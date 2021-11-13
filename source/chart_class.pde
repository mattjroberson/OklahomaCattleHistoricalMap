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
  
  color red, blue, lineColor;
  
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
    
    chartScale = getChartScale(.1);
    
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
  void plotTotalChart(ArrayList<Float> data, color col) {
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

  void plotPercentageChart(ArrayList<Float> data) {
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

        color col;

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

  void plotLine(float x1, float y1, float x2, float y2, color col) {
    stroke(col);
    line(offsetX+x1, offsetY+y1, offsetX+x2, offsetY+y2);
  }
  
  void drawMarkerLines(boolean isPercent){
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
  
  void drawSingleMarker(float val, boolean isPercent){
    float yValue = map(val, 0, 1, 0, chartHeight);
    plotLine(0, yValue, chartWidth, yValue, lineColor);
    
    int shiftY = isPercent == true ? 4 : 2;
    
    Object textVal;
    
    if(isPercent == true){
      textVal = val;
    }
    else{
     float calcVal = (1 - val) * chartScale;
     textVal = (int)(Math.round( calcVal / 100.0) * 100);
    }
    
    textSize(12);
    textAlign(RIGHT);
    fill(lineColor);
    text(String.valueOf(textVal), offsetX + chartWidth - 2, offsetY + chartHeight - yValue - shiftY);
    textAlign(LEFT);
    text(String.valueOf(textVal), offsetX + 4, offsetY + chartHeight - yValue - shiftY);
  }
  
  boolean mousePressed(){
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
  
  void mouseReleased(){
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
  void render() {
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
