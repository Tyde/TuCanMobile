/**
 *	This file is part of TuCan Mobile.
 *
 *	TuCan Mobile is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	TuCan Mobile is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with TuCan Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dalthed.tucan.adapters;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
/**
 * Verbindet zwei verschiedene Adapter in einem 
 * @author Tyde
 *
 */
public class MergedAdapter extends BaseAdapter {
	private ListAdapter firstAdapter, secondAdapter;
	/**
	 * Erstellt einen neuen Adapter, welcher zuerst alle Views des ersten dann alle Views des zweiten darstellt
	 * @param firstAdapter erster Adapter
	 * @param secondAdapter zweiter Adapter
	 */
	public MergedAdapter(ListAdapter firstAdapter, ListAdapter secondAdapter) {
		if (firstAdapter != null && secondAdapter != null) {
			this.firstAdapter = firstAdapter;
			this.secondAdapter = secondAdapter;
		}
	}
	

	@Override
	public int getCount() {
		//Anzahl addieren
		return firstAdapter.getCount() + secondAdapter.getCount();
	}

	@Override
	public Object getItem(int position) {
		//anfangs nur ersten Adapter, dann zweiter Adapter
		if(position<firstAdapter.getCount()){
			return firstAdapter.getItem(position);
		}else {
			//Neue position muss berechent werden sonst out of bounds
			int newPosition = position-firstAdapter.getCount();
			return secondAdapter.getItem(newPosition);
		}
		
	}

	@Override
	public long getItemId(int position) {
		
		if(position<firstAdapter.getCount()){
			return firstAdapter.getItemId(position);
		}else {
			int newPosition = position-firstAdapter.getCount();
			return secondAdapter.getItemId(newPosition);
		}
	}

	@Override
	public int getItemViewType(int position) {
		if(position<firstAdapter.getCount()){
			return firstAdapter.getItemViewType(position);
		}else {
			int newPosition = position-firstAdapter.getCount();
			return secondAdapter.getItemViewType(newPosition)+firstAdapter.getViewTypeCount();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(position<firstAdapter.getCount()){
			return firstAdapter.getView(position,convertView,parent);
		}else {
			
			int newPosition = position-firstAdapter.getCount();
			return secondAdapter.getView(newPosition,convertView,parent);
		}
	}

	@Override
	public int getViewTypeCount() {
		return firstAdapter.getViewTypeCount()+secondAdapter.getViewTypeCount();
	}

	@Override
	public boolean hasStableIds() {
		return (firstAdapter.hasStableIds() && secondAdapter.hasStableIds());
	}

	@Override
	public boolean isEmpty() {
		return (firstAdapter.isEmpty() && secondAdapter.isEmpty());
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		firstAdapter.registerDataSetObserver(observer);
		secondAdapter.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		firstAdapter.unregisterDataSetObserver(observer);
		secondAdapter.unregisterDataSetObserver(observer);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return (firstAdapter.areAllItemsEnabled() && secondAdapter.areAllItemsEnabled());
	}

	@Override
	public boolean isEnabled(int position) {
		if(position<firstAdapter.getCount()){
			return firstAdapter.isEnabled(position);
		}else {
			int newPosition = position-firstAdapter.getCount();
			return secondAdapter.isEnabled(newPosition);
		}
	}

}
