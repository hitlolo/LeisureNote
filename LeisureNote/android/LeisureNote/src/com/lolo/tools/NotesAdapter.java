package com.lolo.tools;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lolo.dev.R;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class NotesAdapter extends SimpleAdapter 
{
	private Context myContext;
	private List<Map<String, Object>> list;
	
 
	
	public NotesAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) 
	{
		
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		
		list      = (List<Map<String, Object>>) data;
		myContext = context;
		
//		SimpleAdapter.ViewBinder binder = new SimpleAdapter.ViewBinder() {
//
//            @Override
//            public boolean setViewValue(View view, Object data, String textRepresentation) 
//            {
//                if (view instanceof ImageView ) 
//                {
//                	Log.i("list","view id:"+view.getId());
//                	boolean isShow = false;
//                	if(data instanceof Boolean)
//                	{
//                		Log.i("list","bool value:"+((Boolean) data).booleanValue());
//                		isShow = ((Boolean) data).booleanValue();
//                		
//                		if(isShow)
//                    	{
//                    		view.setVisibility(View.VISIBLE);
//                    	}
//                    	if(!isShow)
//                    	{
//                    		view.setVisibility(View.GONE);
//                    	}
//                	}
//                    
//                	
//                 
//                }
//                    return false;           
//            }
//		};
//        setViewBinder(binder);
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
	         convertView = factory.inflate(R.layout.list_item,null);  
	         view = new ViewClass();  
	         view.title = (TextView)convertView.findViewById(R.id.list_title);  
	         view.content = (TextView)convertView.findViewById(R.id.list_content);
	         view.ctime = (TextView)convertView.findViewById(R.id.list_time);
	         view.image = (ImageView)convertView.findViewById(R.id.list_image_item);
	         view.audio = (ImageView)convertView.findViewById(R.id.list_sound_item);
	         view.hand  = (ImageView)convertView.findViewById(R.id.list_hand_item);      
	         convertView.setTag(view);  
	       }else{  
	          view =(ViewClass) convertView.getTag();          
	       }
	      
	      view.setText(list, position);
	      view.setImage(list, position);
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



//	@Override 
//	
//	public boolean isEnabled(int position) { 
//
//         // TODO Auto-generated method stub 
//
//         if (position % 2 == 0) 
//         {// 
//        	 return false; 
//         } 
//         else 
//         { 
//             return true; 
//         }
//         
//	} 
	 
	static class ViewClass
	{  
		TextView title;
		TextView content;
	    TextView ctime;
	    ImageView image;
	    ImageView audio;
	    ImageView hand;	      
	    
	    public void setText(List<Map<String,Object>> list,int position)
	    {
	    	
	    	this.title.setText(list.get(position).get(Sync.TITLE).toString());
	    	this.content.setText((String)list.get(position).get(Sync.TEXT));
	    	this.ctime.setText(list.get(position).get(Sync.CTIME).toString());
	    }
	    
	    public void setImage(List<Map<String,Object>> list,int position)
	    {
	    	if(list.get(position).get(Sync.IMAGE)!=null)
	    	{
	    		this.image.setVisibility(View.VISIBLE);
	    		SpannableStringBuilder sbuilder = new SpannableStringBuilder(content.getText());
//	    		int start = sbuilder.length();
//	    		int start = content.getSelectionEnd();
//	    		int end   = start + tempImageName.length();
//	    		sbuilder.insert(start, tempImageName);
	    		byte[] b = (byte[]) list.get(position).get(Sync.IMAGE);
	    		MyMap  m = (MyMap) TypeTransformer.BytesToObject(b);
	    		Set<String> s = m.keySet();
	    		Object[] sa = s.toArray();
	    		
	    		for (int i = 0; i < sa.length; i++) 
	    		{
	    			Pattern p = Pattern.compile((String)sa[i]);  
		    	    Matcher match = p.matcher(sbuilder);  
		    	        if (match.find()) 
		    	        {  
//		    	            span = new ForegroundColorSpan(Color.RED);//需要重复！
		    	        	ImageSpan imSpan = new ImageSpan(m.get(sa[i]).getBitmap(),ImageSpan.ALIGN_BASELINE);
		    	          //span = new ImageSpan(drawable,ImageSpan.XX);//设置现在图片
		    	        	sbuilder.setSpan(imSpan, match.start(), match.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
		    	        }  
				}
	    		
	    		content.setText(sbuilder);  
	    		
//	    		sbuilder.setSpan(imSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
	    	}
	    	else
	    	{
	    		this.image.setVisibility(View.GONE);
	    	}
	    	
	    	if(list.get(position).get(Sync.AUDIO)!=null)
	    	{
	    		this.audio.setVisibility(View.VISIBLE);
	    	}
	    	else
	    	{
	    		this.audio.setVisibility(View.GONE);
	    	}
	    	
	    	if(list.get(position).get(Sync.HAND)!=null)
	    	{
	    		this.hand.setVisibility(View.VISIBLE);
	    		doAttachWords(list,position);
	    		
	    	}
	    	else
	    	{
	    		this.hand.setVisibility(View.GONE);
	    	}
	    }
	    
	    /**
		 * 
		 */
		
		public void doAttachWords(List<Map<String,Object>> list,int position)
		{
			SpannableStringBuilder sbuilder = new SpannableStringBuilder(content.getText());
//			int start = sbuilder.length();
//			int start = content.getSelectionEnd();
//			int end   = start + tempImageName.length();
//			sbuilder.insert(start, tempImageName);
			byte[] b = (byte[]) list.get(position).get(Sync.HAND);
			MyMap  m = (MyMap) TypeTransformer.BytesToObject(b);
			Set<String> s = m.keySet();
			Object[] sa = s.toArray();
			
			for (int i = 0; i < sa.length; i++) 
			{
				Pattern p = Pattern.compile((String)sa[i]);  
	    	    Matcher match = p.matcher(sbuilder);  
	    	        if (match.find()) 
	    	        {  
//	    	            span = new ForegroundColorSpan(Color.RED);//需要重复！
	    	        	ImageSpan imSpan = new ImageSpan(m.get(sa[i]).getBitmap(),ImageSpan.ALIGN_BASELINE);
	    	          //span = new ImageSpan(drawable,ImageSpan.XX);//设置现在图片
	    	        	sbuilder.setSpan(imSpan, match.start(), match.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
	    	        }  
			}
			
			content.setText(sbuilder);  
		}
	   
	} 
	

	
}
