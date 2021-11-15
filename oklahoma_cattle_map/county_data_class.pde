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
