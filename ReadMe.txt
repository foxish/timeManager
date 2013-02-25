##TimeTracker (https://play.google.com/store/apps/details?id=com.anirudhr.timeMan)

Aims at:
 - Acting like an activities list
 - A way to keep-track of what you did and get statistical data (efficiency etc)
 - Archive old data at user-specified time (00:00 by default)

Compiled against ABS v4.2.0 and aChartEngine v1.0.0 (prerequisites to build)

 - History.java -> A structure to store a single history item
 - Main.java -> Main class, serves as the host for other fragments
 - PieChart.java -> AChartEngine's PieChart Intent is fired from this
 - PrefActivity.java -> Preferences
 - StatisticsFragment.java -> Maintains statistics (parsed from XML)
 - TaskDetailActivity.java -> Editing/Addition of a timer brings up this menu
 - TimeListAdapter.java -> Cursor adapter for the below page
 - TimeListFragment.java -> Main page. List of timers
 - TimeUtilites.java -> A collection of a lot of ultility functions to convert time to different formats
 - XmlUtilites.java -> The archived data is stored as XML. Reading and writing that are done by this class		
 - globalAccess.java -> shameful things that must never have been written this way. No memory leak. 
                        Only context is stored. Will refactor this BS "someday" (read never).
		

 db/
  - MyTodoContentProvider.java -> Content provider used for database interaction
  - TodoTable.java -> The table, in the database, storing all that data
  - myDatabase.java -> The database class

 widget/
  - WidgetProvider.java  -> Yeah, it says what it does
  - WidgetService.java -> Background service
