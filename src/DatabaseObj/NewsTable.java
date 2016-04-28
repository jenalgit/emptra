package DatabaseObj;

import android.util.Log;

import com.orm.SugarRecord;
//import com.orm.dsl.Column;
//import com.orm.dsl.Table;

//@Table(name = "NewsTable")

public class NewsTable extends SugarRecord {
  private String title;
  private String desc;
  public NewsTable(){
    this.desc="none";
    this.title="none";
    Log.i("News", "Constructor()" + this.desc +" " + this.title);
  }
  public NewsTable(String title, String desc) {
    this.desc=desc;
    this.title = title;
    Log.i("News", "Constructor" + this.desc +" " + this.title);
  }
  public static String findDescId(Class<NewsTable> class1, Long i) {
    Log.i("News", "findDescId");
    String rString="~None";
    
    if(class1 == null)
    {
      Log.w("News", "Empty class found : Null");
    }
    try{
      NewsTable lNews = SugarRecord.findById(class1, i);
    Log.i("News", "Set : "+lNews.title +","+lNews.desc);
    rString = lNews.desc;
    }catch(Exception e)
    {Log.w("News", "EXCEPTION : " +e.getMessage());}
    return rString;
  }
  
}

