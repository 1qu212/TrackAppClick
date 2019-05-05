package cn.xydzjnq.track.api;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Keep;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.ToggleButton;

import org.json.JSONObject;

import java.util.Locale;

public class TrackHelper {
    @Keep
    public static void trackViewOnClick(DialogInterface dialogInterface, int whichButton) {
        try {
            Dialog dialog = null;
            if (dialogInterface instanceof Dialog) {
                dialog = (Dialog) dialogInterface;
            }

            if (dialog == null) {
                return;
            }

            Context context = dialog.getContext();
            //将Context转成Activity
            Activity activity = TrackPrivateAPI.getActivityFromContext(context);

            if (activity == null) {
                activity = dialog.getOwnerActivity();
            }

            JSONObject properties = new JSONObject();
            //$screen_name & $title
            if (activity != null) {
                properties.put("$activity", activity.getClass().getCanonicalName());
            }

            Button button = null;
            if (dialog instanceof android.app.AlertDialog) {
                button = ((android.app.AlertDialog) dialog).getButton(whichButton);
            } else if (dialog instanceof android.support.v7.app.AlertDialog) {
                button = ((android.support.v7.app.AlertDialog) dialog).getButton(whichButton);
            }

            if (button != null) {
                properties.put("$element_content", button.getText());
            }

            properties.put("$element_type", "Dialog");

            TrackAPI.getInstance().track("$AppClick", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trackViewOnClick(CompoundButton view, boolean isChecked) {
        try {
            Context context = view.getContext();
            if (context == null) {
                return;
            }

            JSONObject properties = new JSONObject();

            Activity activity = TrackPrivateAPI.getActivityFromContext(context);

            try {
                String idString = context.getResources().getResourceEntryName(view.getId());
                if (!TextUtils.isEmpty(idString)) {
                    properties.put("$element_id", idString);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (activity != null) {
                properties.put("$activity", activity.getClass().getCanonicalName());
            }

            String viewText = null;
            String viewType;
            if (view instanceof CheckBox) {
                viewType = "CheckBox";
                CheckBox checkBox = (CheckBox) view;
                if (!TextUtils.isEmpty(checkBox.getText())) {
                    viewText = checkBox.getText().toString();
                }
            } else if (view instanceof SwitchCompat) {
                viewType = "SwitchCompat";
                SwitchCompat switchCompat = (SwitchCompat) view;
                if (isChecked) {
                    if (!TextUtils.isEmpty(switchCompat.getTextOn())) {
                        viewText = switchCompat.getTextOn().toString();
                    }
                } else {
                    if (!TextUtils.isEmpty(switchCompat.getTextOff())) {
                        viewText = switchCompat.getTextOff().toString();
                    }
                }
            } else if (view instanceof ToggleButton) {
                viewType = "ToggleButton";
                ToggleButton toggleButton = (ToggleButton) view;
                if (isChecked) {
                    if (!TextUtils.isEmpty(toggleButton.getTextOn())) {
                        viewText = toggleButton.getTextOn().toString();
                    }
                } else {
                    if (!TextUtils.isEmpty(toggleButton.getTextOff())) {
                        viewText = toggleButton.getTextOff().toString();
                    }
                }
            } else if (view instanceof RadioButton) {
                viewType = "RadioButton";
                RadioButton radioButton = (RadioButton) view;
                if (!TextUtils.isEmpty(radioButton.getText())) {
                    viewText = radioButton.getText().toString();
                }
            } else {
                viewType = view.getClass().getCanonicalName();
            }

            //Content
            if (!TextUtils.isEmpty(viewText)) {
                properties.put("$element_content", viewText);
            }

            if (!TextUtils.isEmpty(viewType)) {
                properties.put("$element_type", viewType);
            }

            properties.put("isChecked", isChecked);

            TrackAPI.getInstance().track("$AppClick", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Keep
    public static void trackViewOnClick(DialogInterface dialogInterface, int whichButton, boolean isChecked) {
        try {
            Dialog dialog = null;
            if (dialogInterface instanceof Dialog) {
                dialog = (Dialog) dialogInterface;
            }

            if (dialog == null) {
                return;
            }

            Context context = dialog.getContext();
            //将Context转成Activity
            Activity activity = TrackPrivateAPI.getActivityFromContext(context);

            if (activity == null) {
                activity = dialog.getOwnerActivity();
            }

            JSONObject properties = new JSONObject();
            //$screen_name & $title
            if (activity != null) {
                properties.put("$activity", activity.getClass().getCanonicalName());
            }

            ListView listView = null;
            if (dialog instanceof android.app.AlertDialog) {
                listView = ((android.app.AlertDialog) dialog).getListView();
            } else if (dialog instanceof android.support.v7.app.AlertDialog) {
                listView = ((android.support.v7.app.AlertDialog) dialog).getListView();
            }

            if (listView != null) {
                ListAdapter listAdapter = listView.getAdapter();
                Object object = listAdapter.getItem(whichButton);
                if (object != null) {
                    if (object instanceof String) {
                        properties.put("$element_content", object);
                    }
                }
            }

            properties.put("isChecked", isChecked);
            properties.put("$element_type", "Dialog");

            TrackAPI.getInstance().track("$AppClick", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * MenuItem 被点击，自动埋点
     *
     * @param object   Object
     * @param menuItem MenuItem
     */
    @Keep
    public static void trackViewOnClick(Object object, MenuItem menuItem) {
        try {
            Context context = null;
            if (object instanceof Context) {
                context = (Context) object;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("$element_type", "menuItem");

            jsonObject.put("$element_content", menuItem.getTitle());

            if (context != null) {
                String idString = null;
                try {
                    idString = context.getResources().getResourceEntryName(menuItem.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(idString)) {
                    jsonObject.put("$element_id", idString);
                }

                Activity activity = TrackPrivateAPI.getActivityFromContext(context);
                if (activity != null) {
                    jsonObject.put("$activity", activity.getClass().getCanonicalName());
                }
            }

            TrackAPI.getInstance().track("$AppClick", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Keep
    public static void trackViewOnClick(MenuItem menuItem) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("$element_type", "menuItem");

            jsonObject.put("$element_content", menuItem.getTitle());

            TrackAPI.getInstance().track("$AppClick", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Keep
    public static void trackViewOnClick(String tabName) {
        trackTabHost(tabName);
    }

    @Keep
    public static void trackTabHost(String tabName) {
        try {
            JSONObject properties = new JSONObject();

            properties.put("$element_type", "TabHost");
            properties.put("$element_content", tabName);
            TrackAPI.getInstance().track("$AppClick", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Keep
    public static void trackViewOnClick(ExpandableListView expandableListView, View view,
                                        int groupPosition, long id) {
        trackExpandableListViewChildOnClick(expandableListView, view, groupPosition, -1);
    }

    @Keep
    public static void trackViewOnClick(ExpandableListView expandableListView, View view,
                                        int groupPosition, int childPosition, long id) {
        trackExpandableListViewChildOnClick(expandableListView, view, groupPosition, childPosition);
    }

    @Keep
    public static void trackExpandableListViewGroupOnClick(ExpandableListView expandableListView, View view,
                                                           int groupPosition) {
        trackExpandableListViewChildOnClick(expandableListView, view, groupPosition, -1);
    }

    @Keep
    public static void trackExpandableListViewChildOnClick(ExpandableListView expandableListView, View view,
                                                           int groupPosition, int childPosition) {
        try {
            Context context = expandableListView.getContext();
            if (context == null) {
                return;
            }

            JSONObject properties = new JSONObject();
            Activity activity = TrackPrivateAPI.getActivityFromContext(context);
            if (activity != null) {
                properties.put("$activity", activity.getClass().getCanonicalName());
            }

            if (childPosition != -1) {
                properties.put("$element_position", String.format(Locale.CHINA, "%d:%d", groupPosition, childPosition));
            } else {
                properties.put("$element_position", String.format(Locale.CHINA, "%d", groupPosition));
            }

            String idString = TrackPrivateAPI.getViewId(expandableListView);
            if (!TextUtils.isEmpty(idString)) {
                properties.put("$element_id", idString);
            }

            properties.put("$element_type", "ExpandableListView");

            String viewText = null;
            if (view instanceof ViewGroup) {
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    viewText = TrackPrivateAPI.traverseViewContent(stringBuilder, (ViewGroup) view);
                    if (!TextUtils.isEmpty(viewText)) {
                        viewText = viewText.substring(0, viewText.length() - 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (!TextUtils.isEmpty(viewText)) {
                properties.put("$element_content", viewText);
            }

            TrackAPI.getInstance().track("$AppClick", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Keep
    public static void trackViewOnClick(android.widget.AdapterView adapterView, android.view.View view, int position, long id) {
        trackViewOnClick(adapterView, view, position);
    }

    @Keep
    public static void trackViewOnClick(android.widget.AdapterView adapterView, android.view.View view, int position) {
        try {
            Context context = adapterView.getContext();
            if (context == null) {
                return;
            }

            JSONObject properties = new JSONObject();

            Activity activity = TrackPrivateAPI.getActivityFromContext(context);
            String idString = TrackPrivateAPI.getViewId(adapterView);
            if (!TextUtils.isEmpty(idString)) {
                properties.put("$element_id", idString);
            }

            if (activity != null) {
                properties.put("$activity", activity.getClass().getCanonicalName());
            }
            properties.put("$element_position", String.valueOf(position));

            if (adapterView instanceof Spinner) {
                properties.put("$element_type", "Spinner");
                Object item = adapterView.getItemAtPosition(position);
                if (item != null) {
                    if (item instanceof String) {
                        properties.put("$element_content", item);
                    }
                }
            } else {
                if (adapterView instanceof ListView) {
                    properties.put("$element_type", "ListView");
                } else if (adapterView instanceof GridView) {
                    properties.put("$element_type", "GridView");
                }

                String viewText = null;
                if (view instanceof ViewGroup) {
                    try {
                        StringBuilder stringBuilder = new StringBuilder();
                        viewText = TrackPrivateAPI.traverseViewContent(stringBuilder, (ViewGroup) view);
                        if (!TextUtils.isEmpty(viewText)) {
                            viewText = viewText.substring(0, viewText.length() - 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    viewText = TrackPrivateAPI.getElementContent(view);
                }
                //$element_content
                if (!TextUtils.isEmpty(viewText)) {
                    properties.put("$element_content", viewText);
                }
            }
            TrackAPI.getInstance().track("$AppClick", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Keep
    public static void trackViewOnClick(RatingBar ratingBar, float v, boolean b) {
        trackViewOnClick(ratingBar);
    }

    /**
     * View 被点击，自动埋点
     *
     * @param view View
     */
    @Keep
    public static void trackViewOnClick(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("$element_type", TrackPrivateAPI.getElementType(view));
            jsonObject.put("$element_id", TrackPrivateAPI.getViewId(view));
            jsonObject.put("$element_content", TrackPrivateAPI.getElementContent(view));

            Activity activity = TrackPrivateAPI.getActivityFromView(view);
            if (activity != null) {
                jsonObject.put("$activity", activity.getClass().getCanonicalName());
            }

            TrackAPI.getInstance().track("$AppClick", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
