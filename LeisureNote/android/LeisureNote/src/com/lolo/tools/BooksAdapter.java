package com.lolo.tools;

import java.util.List;
import java.util.Map;
import com.lolo.dev.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class BooksAdapter extends SimpleAdapter 
{
	private Context myContext;
	private List<Map<String, Object>> list;
	
 
	
	@SuppressWarnings("unchecked")
	public BooksAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) 
	{
		
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		
		list      = (List<Map<String, Object>>) data;
		myContext = context;
		
	}
	
	 @Override
	public View getView(int position, View convertView, ViewGroup parent) 
	 {
        // TODO Auto-generated method stub
//		 View localView;
//
//		 return super.getView(position, convertView, parent);
		  ViewClass view ;  
	      if(convertView == null){  
	         LayoutInflater factory = LayoutInflater.from(myContext);  
	         convertView = factory.inflate(R.layout.book_item,null);  
//	         convertView.setLayoutParams(new LayoutParams(460,158));
	         view = new ViewClass();  
	         view.title    = (TextView)convertView.findViewById(R.id.text_bookname);  
	         view.notesNUm = (TextView)convertView.findViewById(R.id.text_notes_num);
	         view.ctime    = (TextView)convertView.findViewById(R.id.text_booktime);
	  
	        
	         convertView.setTag(view);  
	       }else{  
	          view =(ViewClass) convertView.getTag(); 
	          
	       }
	      
	      view.setText(list, position);
	      return convertView;
        
	}
	 
	
		
	public void notifyDataSetChanged()
	{
		super.notifyDataSetChanged();
	}
		
//	 public void onListItemClick(ListView parent, View view, int position, long id){
//		 Toast.makeText(myContext, ""+position, Toast.LENGTH_SHORT).show();
//		 }
	 
	 public boolean areAllItemsEnabled() { 

         // TODO Auto-generated method stub 

         return false; 
       
	 } 
	 
	static class ViewClass
	{  
		TextView title;
	    TextView ctime;
	    TextView notesNUm;
	    
	    public void setText(List<Map<String,Object>> list,int position)
	    {
	    	
	    	this.title.setText(list.get(position).get(Sync.BOOK_NAME).toString());
	    	this.notesNUm.setText((String)list.get(position).get(Sync.BOOK_NOTES_NUM).toString());
	    	this.ctime.setText(list.get(position).get(Sync.BOOK_CTIME).toString());
	    }
	    
	   
	}
	  
	
}
