package com.highfaner.highfaner.SwipeMenuListView;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.adapter.EaseConversationAdapater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author baoyz
 * @date 2014-8-18
 * 
 */
public class SwipeMenuListView extends ListView {

	private static final int TOUCH_STATE_NONE = 0;
	private static final int TOUCH_STATE_X = 1;
	private static final int TOUCH_STATE_Y = 2;

	private int MAX_Y = 5;
	private int MAX_X = 3;
	private float mDownX;
	private float mDownY;
	private int mTouchState;
	private int mTouchPosition;
	private SwipeMenuLayout mTouchView;
	private OnSwipeListener mOnSwipeListener;

	private SwipeMenuCreator mMenuCreator;
	private OnMenuItemClickListener mOnMenuItemClickListener;
	private Interpolator mCloseInterpolator;
	private Interpolator mOpenInterpolator;


	/**
	 * 加载数据
	 * @param context
     */

	protected int primaryColor;
	protected int secondaryColor;
	protected int timeColor;
	protected int primarySize;
	protected int secondarySize;
	protected float timeSize;


	protected final int MSG_REFRESH_ADAPTER_DATA = 0;

	protected Context context;
	protected EaseConversationAdapater adapter;
	protected List<EMConversation> conversations = new ArrayList<EMConversation>();
	protected List<EMConversation> passedListRef = null;

	public SwipeMenuListView(Context context) {
		super(context);
		init(context,null);
	}

	public SwipeMenuListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public SwipeMenuListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		MAX_X = dp2px(MAX_X);
		MAX_Y = dp2px(MAX_Y);
		mTouchState = TOUCH_STATE_NONE;

		this.context = context;
		TypedArray ta = context.obtainStyledAttributes(attrs, com.hyphenate.easeui.R.styleable.EaseConversationList);
		primaryColor = ta.getColor(com.hyphenate.easeui.R.styleable.EaseConversationList_cvsListPrimaryTextColor, getResources().getColor(com.hyphenate.easeui.R.color.list_itease_primary_color));
		secondaryColor = ta.getColor(com.hyphenate.easeui.R.styleable.EaseConversationList_cvsListSecondaryTextColor, getResources().getColor(com.hyphenate.easeui.R.color.list_itease_secondary_color));
		timeColor = ta.getColor(com.hyphenate.easeui.R.styleable.EaseConversationList_cvsListTimeTextColor, getResources().getColor(com.hyphenate.easeui.R.color.list_itease_secondary_color));
		primarySize = ta.getDimensionPixelSize(com.hyphenate.easeui.R.styleable.EaseConversationList_cvsListPrimaryTextSize, 0);
		secondarySize = ta.getDimensionPixelSize(com.hyphenate.easeui.R.styleable.EaseConversationList_cvsListSecondaryTextSize, 0);
		timeSize = ta.getDimension(com.hyphenate.easeui.R.styleable.EaseConversationList_cvsListTimeTextSize, 0);

		ta.recycle();

	}

	public void init(List<EMConversation> conversationList){
		this.init(conversationList, null);
	}

	public void init(List<EMConversation> conversationList, EaseConversationListHelper helper){
		conversations = conversationList;
		if(helper != null){
			this.conversationListHelper = helper;
		}
		adapter = new EaseConversationAdapater(context, 0, conversationList);
//        adapter.setCvsListHelper(conversationListHelper);
		adapter.setPrimaryColor(primaryColor);
		adapter.setPrimarySize(primarySize);
		adapter.setSecondaryColor(secondaryColor);
		adapter.setSecondarySize(secondarySize);
		adapter.setTimeColor(timeColor);
		adapter.setTimeSize(timeSize);
		setAdapter(adapter);
	}



	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(new SwipeMenuAdapter(getContext(), adapter) {
			@Override
			public void createMenu(SwipeMenu menu) {
				if (mMenuCreator != null) {
					mMenuCreator.create(menu);
				}
			}

			@Override
			public void onItemClick(SwipeMenuView view, SwipeMenu menu,
									int index) {
				if (mOnMenuItemClickListener != null) {
					mOnMenuItemClickListener.onMenuItemClick(
							view.getPosition(), menu, index);
				}
				if (mTouchView != null) {
					mTouchView.smoothCloseMenu();
				}
			}
		});
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
				case MSG_REFRESH_ADAPTER_DATA:
					if (adapter != null) {
						adapter.notifyDataSetChanged();
					}
					break;
				default:
					break;
			}
		}
	};


	/**
	 * load conversations
	 *
	 * @param context
	 * @return
	+    */
	private List<EMConversation> loadConversationsWithRecentChat() {
		Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		/**
		 * lastMsgTime will change if there is new message during sorting
		 * so use synchronized to make sure timestamp of last message won't change.
		 */
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
				}
			}
		}
		try {
			// Internal is TimSort algorithm, has bug
			sortConversationByLastChatTime(sortList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}
		return list;
	}

	/**
	 * sorting according timestamp of last message
	 *
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
			@Override
			public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

				if (con1.first == con2.first) {
					return 0;
				} else if (con2.first > con1.first) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}

	public EMConversation getItem(int position) {
		return (EMConversation)adapter.getItem(position);
	}

	public void refresh() {
		if(!handler.hasMessages(MSG_REFRESH_ADAPTER_DATA)){
			handler.sendEmptyMessage(MSG_REFRESH_ADAPTER_DATA);
		}
	}

	public void filter(CharSequence str) {
		adapter.getFilter().filter(str);
	}

	public void setCloseInterpolator(Interpolator interpolator) {
		mCloseInterpolator = interpolator;
	}

	public void setOpenInterpolator(Interpolator interpolator) {
		mOpenInterpolator = interpolator;
	}

	public Interpolator getOpenInterpolator() {
		return mOpenInterpolator;
	}

	public Interpolator getCloseInterpolator() {
		return mCloseInterpolator;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null)
			return super.onTouchEvent(ev);
		int action = MotionEventCompat.getActionMasked(ev);
		action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			int oldPos = mTouchPosition;
			mDownX = ev.getX();
			mDownY = ev.getY();
			mTouchState = TOUCH_STATE_NONE;

			mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

			if (mTouchPosition == oldPos && mTouchView != null
					&& mTouchView.isOpen()) {
				mTouchState = TOUCH_STATE_X;
				mTouchView.onSwipe(ev);
				return true;
			}

			View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

			if (mTouchView != null && mTouchView.isOpen()) {
				mTouchView.smoothCloseMenu();
				mTouchView = null;
				return super.onTouchEvent(ev);
			}
			if (view instanceof SwipeMenuLayout) {
				mTouchView = (SwipeMenuLayout) view;
			}
			if (mTouchView != null) {
				mTouchView.onSwipe(ev);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			float dy = Math.abs((ev.getY() - mDownY));
			float dx = Math.abs((ev.getX() - mDownX));
			if (mTouchState == TOUCH_STATE_X) {
				if (mTouchView != null) {
					mTouchView.onSwipe(ev);
				}
				getSelector().setState(new int[] { 0 });
				ev.setAction(MotionEvent.ACTION_CANCEL);
				super.onTouchEvent(ev);
				return true;
			} else if (mTouchState == TOUCH_STATE_NONE) {
				if (Math.abs(dy) > MAX_Y) {
					mTouchState = TOUCH_STATE_Y;
				} else if (dx > MAX_X) {
					mTouchState = TOUCH_STATE_X;
					if (mOnSwipeListener != null) {
						mOnSwipeListener.onSwipeStart(mTouchPosition);
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_X) {
				if (mTouchView != null) {
					mTouchView.onSwipe(ev);
					if (!mTouchView.isOpen()) {
						mTouchPosition = -1;
						mTouchView = null;
					}
				}
				if (mOnSwipeListener != null) {
					mOnSwipeListener.onSwipeEnd(mTouchPosition);
				}
				ev.setAction(MotionEvent.ACTION_CANCEL);
				super.onTouchEvent(ev);
				return true;
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	public void smoothOpenMenu(int position) {
		if (position >= getFirstVisiblePosition()
				&& position <= getLastVisiblePosition()) {
			View view = getChildAt(position - getFirstVisiblePosition());
			if (view instanceof SwipeMenuLayout) {
				mTouchPosition = position;
				if (mTouchView != null && mTouchView.isOpen()) {
					mTouchView.smoothCloseMenu();
				}
				mTouchView = (SwipeMenuLayout) view;
				mTouchView.smoothOpenMenu();
			}
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getContext().getResources().getDisplayMetrics());
	}

	public void setMenuCreator(SwipeMenuCreator menuCreator) {
		this.mMenuCreator = menuCreator;
	}

	public void setOnMenuItemClickListener(
			OnMenuItemClickListener onMenuItemClickListener) {
		this.mOnMenuItemClickListener = onMenuItemClickListener;
	}

	public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
		this.mOnSwipeListener = onSwipeListener;
	}

	public static interface OnMenuItemClickListener {
		void onMenuItemClick(int position, SwipeMenu menu, int index);
	}

	public static interface OnSwipeListener {
		void onSwipeStart(int position);

		void onSwipeEnd(int position);
	}

	private EaseConversationListHelper conversationListHelper;

	public interface EaseConversationListHelper{
		/**
		 * set content of second line
		 * @param lastMessage
		 * @return
		 */
		String onSetItemSecondaryText(EMMessage lastMessage);
	}
	public void setConversationListHelper(EaseConversationListHelper helper){
		conversationListHelper = helper;
	}
}
