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
