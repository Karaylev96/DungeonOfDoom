///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package client;///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////

import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class ObserverMain extends JLabel implements Observer
{
	//privates//har
	private static final long serialVersionUID = 666;//the number of the beast
	private String imageOn;
	private String imageOff;
	private boolean active;

	public ObserverMain(String imgon, String imgoff, String text, boolean active) {
		this.imageOn = imgon;
		this.imageOff = imgoff;
		this.active = active;		
		setText(text);
		setHorizontalTextPosition(JLabel.CENTER);
		setVerticalTextPosition(JLabel.BOTTOM);
		
		if (active) 
		{
			setIcon(new ImageIcon(imgon));
		}
		
		else
		{
			setIcon(new ImageIcon(imgoff));
		}
	}

	public String getImageOn()
	{
		return imageOn;
	}

	public void setImageOn(String imageOn) 
	{
		this.imageOn = imageOn;
	}

	public String getImageOff() 
	{
		return imageOff;
	}

	
	public void setImageOff(String imageOff) 
	{
		this.imageOff = imageOff;
	}


	public boolean isActive() 
	{
		return active;
	}

	public void setActive(boolean active) 
	{
		this.active = active;
		
		if (active) 
		{
			setIcon(new ImageIcon(imageOn));
		} 
		
		else
		{
			setIcon(new ImageIcon(imageOff));
		}
	}


	public void update(Observable observable, Object value) 
	{
		boolean active = (Boolean) value;
		
		if (active) 
		{
			setIcon(new ImageIcon(imageOn));
		} 
		
		else 
		{
			setIcon(new ImageIcon(imageOff));
		}
	}
}

class ObservableArrayList<E> extends ArrayList<E>
{
	private static final long serialVersionUID = 666;///ho ho ho the number of the beast
	private MyObservable observable;
	public ObservableArrayList() 
	{
		observable = new MyObservable();
	}
	public void addObserver(Observer o)
	{
		observable.addObserver(o);
	}
	public boolean add(E e)
	{
		boolean result = super.add(e);
		observable.setChanged();
		observable.notifyObservers(this.toArray());
		
		return result;
	}
	
	public void add(int index, E element) //add the elements
	{
		super.add(index, element);
		observable.setChanged();
		observable.notifyObservers(this.toArray());
	}
	
	public boolean addAll(Collection<? extends E> c)//adds and returns 
	{
		boolean result = super.addAll(c);		
		observable.setChanged();
		observable.notifyObservers(this.toArray());		
		return result;
	}
	
	public boolean addAll(int index, Collection<? extends E> c)
	{
		boolean result = super.addAll(index, c);
		observable.setChanged();
		observable.notifyObservers(this.toArray());
		
		return result;
	}
	

	public void clear() 
	{
		super.clear();		
		observable.setChanged();
		observable.notifyObservers(this.toArray());
	}
	

	public E remove(int index)//removes element
	{
		E result = super.remove(index);		
		observable.setChanged();
		observable.notifyObservers(this.toArray());
		
		return result;
	}
	
	
	public boolean remove(Object o) //removes object
	{
		boolean result = super.remove(o);		
		observable.setChanged();
		observable.notifyObservers(this.toArray());		
		return result;
	}
	
	
	public E set(int index, E element) 
	{
		E result = super.set(index, element);
		observable.setChanged();
		observable.notifyObservers(this.toArray());
		return result;
	}
	
	private class MyObservable extends Observable
{
	
		public void setChanged()
		{
			super.setChanged();
		}
	}
}

class ObservableValue<E> extends Observable
{
	private E value;
	public ObservableValue(E value)
	{
		this.value = value;
	}
	
	public E getValue()
	{
		return value;
	}
	
	public void setValue(E value) 
	{
		this.value = value;
		setChanged();
		notifyObservers(this.value);
	}
}

class ObserverLabel extends JLabel implements Observer 
{
	private static final long serialVersionUID = 666;
	private String text;
	private String fText;
	private String value;
	
	public ObserverLabel(String preText, String value, String postText)
	{
		this.value = value;//this is the value we are going to operate with
		this.text = preText;
		this.fText = postText;
		setText();
	}
	
	public ObserverLabel(String preText, String value)
	{
		this(preText, value, "");
	}

	public ObserverLabel(String value) 
	{
		this("", value, "");
	}

	private void setText()
	{
		super.setText(text + value + fText);
	}
	
	public void update(Observable observable, Object value)
	{
		this.value = value.toString();
		setText();
	}
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
