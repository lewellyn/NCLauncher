/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.adw.launcher;

import com.handlerexploit.launcher_reloaded.ApplicationInfo;
import com.handlerexploit.launcher_reloaded.DragController;
import com.handlerexploit.launcher_reloaded.DragSource;
import com.handlerexploit.launcher_reloaded.Launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class AllAppsGridView extends GridView implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, DragSource {

	private DragController mDragger;
	private Launcher mLauncher;
	private Paint mPaint;
	private Paint mLabelPaint;

	public AllAppsGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setDither(false);
		mLabelPaint = new Paint();
		mLabelPaint.setDither(false);
	}

	@Override
	protected void onFinishInflate() {
		setOnItemClickListener(this);
		setOnItemLongClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);
		mLauncher.startActivitySafely(app.intent);
		mLauncher.AnimatedDrawerClose(true);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (!view.isInTouchMode())
			return false;

		mDragger.startDrag(view, this, (ApplicationInfo) parent.getItemAtPosition(position), DragController.DRAG_ACTION_COPY);
		mLauncher.AnimatedDrawerClose(true);
		return true;
	}

	@Override
	public void draw(Canvas canvas) {
		int color = 0xFF000000;
		mPaint.setAlpha(255);
		if (getVisibility() == View.VISIBLE) {
			canvas.drawARGB((int) (1.0f * 255), Color.red(color), Color.green(color), Color.blue(color));
			super.draw(canvas);
		}
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		Drawable[] tmp = ((TextView) child).getCompoundDrawables();
		if (Launcher.hideLabels) {
			canvas.save();
			canvas.translate(child.getLeft() + (child.getWidth() / 2) - (tmp[1].getBounds().width() / 2), child.getTop() + child.getPaddingTop());
			tmp[1].draw(canvas);
			canvas.restore();
		} else {
			child.setDrawingCacheEnabled(true);
			if (child.getDrawingCache() != null) {
				mPaint.setAlpha(255);
				canvas.drawBitmap(child.getDrawingCache(), child.getLeft(), child.getTop(), mPaint);
			} else {
				canvas.save();
				canvas.translate(child.getLeft(), child.getTop());
				child.draw(canvas);
				canvas.restore();
			}
		}
		canvas.restoreToCount(canvas.save());
		return true;
	}
	
	@Override
	public void setDragger(DragController dragger) {
		mDragger = dragger;
	}
	
	@Override
	public void setLauncher(Launcher launcher) {
		mLauncher = launcher;
	}

	@Override
	public void onDropCompleted(View target, boolean success) {
		//Ignore
	}
}