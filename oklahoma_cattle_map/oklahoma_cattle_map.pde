import http.requests.*;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Arrays;

int iter = 0;
int maxIter;
boolean goingUp = true;

PImage stateImage;
PImage titleImage;
County currentPressed;

UserInterface userInterface;
Chart stateChart;

PFont yearFont;
color backgroundColor;

ArrayList<Float> stateDairyData, stateBeefData, stateComparedData;

ArrayList<County> countyList;
ArrayList<Story> storyList;

enum ChartType { KEY, TOTALS, PERCENTAGE, STORY };

ChartType chartType;
ChartType lastChartType;

boolean ctrlPressed;

void setup() {
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
    
  size(1380, 825);
    
  frameRate(30);
  smooth();
}

void setChartType(ChartType newType){
  if(newType != ChartType.KEY && newType != ChartType.STORY){
    lastChartType = newType;
  }
  
  chartType = newType;
}

void setToLastChartType(){
  chartType = lastChartType;
}

void loadStateValues(final ArrayList<County> counties){ 
  
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

void keyPressed(){
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

void keyReleased(){
  if(key == CODED){
    if(keyCode == CONTROL){
      ctrlPressed = false;
    }
  }
}

void shift(int dir){
  if(ctrlPressed) 
    iter += dir;
  else{
    int i = countyList.get(0).yearIter + dir;
    i = (int) (i * Math.pow(2, 4));
    
    iter = i;
  }
}

void mouseWheel(MouseEvent event) {
  float e = event.getCount();
  userInterface.pauseState();
  if(0 <= iter + e && maxIter >= iter + e) iter += e;
}

void mousePressed(){  
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

void mouseReleased(){
  if(currentPressed == null) stateChart.mouseReleased();
  else currentPressed.chart.mouseReleased();
  userInterface.mouseReleased();
}

void draw(){
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
void iterateTime(boolean goingForward){
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
