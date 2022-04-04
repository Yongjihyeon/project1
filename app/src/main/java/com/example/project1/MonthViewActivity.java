package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonthViewActivity extends AppCompatActivity {
    private TextView Date; //연 월 나타내는 텍스트뷰
    private GridView gridview;
    private Calendar mCalendar;
    private GridAdapter gridAdapter; //그리드뷰 어댑터
    private ArrayList<String> daylist;//날짜 저장 리스트
    
    int firstday; // 첫날의 요일
    int lastday; //달의 마지막 날짜
    int year; //현재 년도
    int month; //현재 월

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date = (TextView) findViewById(R.id.today);
        gridview = (GridView) findViewById(R.id.gridview);

        long now = System.currentTimeMillis();//오늘 날짜 설정
        final Date date = new Date(now); //date 객체 생성

        //년 월 일을 따로 따로 저장하기 위해 객체 생성
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        Intent intent = getIntent();
        //이전버튼, 다음버튼을 누를 때 직전 액티비티로 부터 받은 year, month 정보
        year = intent.getIntExtra("year", -1);
        month = intent.getIntExtra("month", -1);

        Calendar calendar = Calendar.getInstance();
        if (year == -1 || month == -1) {
            //만약 이전 액티비가 없다면 그냥 현재 연도, 월 정보 가져오기
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
        }

        TextView todayView = (TextView) findViewById(R.id.today);
        //텍스트뷰에 현재 연도, 월 정보 설정
        todayView.setText(String.format("%d년 %d월", year, month + 1));

        daylist = new ArrayList<String>();
        setCalendar(year, month); //daylist를 초기화 히면서 year, month 정보를 넣는다.

        //------------------이전버튼 버튼 클릭이벤트--------------------
        Button btn = (Button) findViewById(R.id.pre); //이전 버튼 눌렀을 시 이벤트 발생.
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (month == 0) { //1월에서 이전버튼 눌렀을때 지난해의 12월로 이동
                    month = 11;
                    year--;
                } else //1월이 아니라면 달수를 하나씩 전으로 이동
                    month--;
                //새로운 인텐트를 띄울 때 MonthViewActivity를 띄우기위한 인텐트 객체.
                Intent intent = new Intent(getApplicationContext(), MonthViewActivity.class);
                //이전버튼을 누를때마다 바뀌는 year과 month정보를 전달
                intent.putExtra("year", year);
                intent.putExtra("month", month);
                startActivity(intent); //intent 시작
                finish();
            }
        });

        //------------------다음버튼 버튼 클릭이벤트--------------------
        btn = (Button) findViewById(R.id.next); //다음 버튼을 누를시 이벤트 발생
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (month == 11) { //12월일경우 다음버튼을 누르면 다음해 1월로 이동
                    month = 0;
                    year++;
                } else //만약 12월이 아닌달이라면 다음달로 하나씩 이돌
                    month++;
                //새로운 인텐트를 띄울 때 MonthViewActivity를 띄우기위한 인텐트 객체.
                Intent intent = new Intent(getApplicationContext(), MonthViewActivity.class);
                //이전버튼을 누를때마다 바뀌는 year과 month정보를 전달
                intent.putExtra("year", year);
                intent.putExtra("month", month);
                startActivity(intent);//intent 시작
                finish();
            }
        });

        //이번달 1일 무슨요일인지 판단
        mCalendar = Calendar.getInstance();
        mCalendar.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
        int dayNum = mCalendar.get(Calendar.DAY_OF_WEEK);
        for (int i = 1; i < dayNum; i++) {//매달 1일과 요일을 일치시키기위한 공백 추가
            daylist.add("");
        }

        //----------------------그리드뷰 토스트 텍스트 설정---------------------------
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //position은 내가 누른 위치.
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Toast.makeText(MonthViewActivity.this, (year) + "/" + (month + 1) + "/" + (position - firstday + 1), Toast.LENGTH_SHORT).show();
            }
        });
        gridAdapter = new GridAdapter(getApplicationContext(), daylist);
        gridview.setAdapter(gridAdapter);
    }

//그리드뷰 어댑터 생성(어댑터 클래스 생성)
private class GridAdapter extends BaseAdapter {
        private final List<String> list;
        private final LayoutInflater inflater;

        public GridAdapter(Context context, List<String> list) {
            //입력받음 daylist로 객체 초기화
            this.list = list;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {//리스트 크기 반환
            return list.size();
        }
        @Override
        public String getItem(int position) {//리스트 해탕 위치에 날짜 반환
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {//리스트의 위치 반환
            return position;
        }

        @Override
        //각각 포지션에 convertview 설정
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {// convertView는 그리드 하나
                convertView = inflater.inflate(R.layout.calendar_gridview, parent, false);
                // calendar_gridview.xml파일을 convertview 객체로 반환
                holder = new ViewHolder();// View를 담고 있는 ViewHolder 객체
                holder.tvItemGridView = (TextView) convertView.findViewById(R.id.tv_item_gridview);
                // holder의 TextView객체에 calendar_gridview.xml의 텍스트뷰를 연결한다
                convertView.setTag(holder);
                // convertView하나마다의 태그에 둘을 연결하기위해 태그를 holder로 설정.
            }
            else {
                holder = (ViewHolder) convertView.getTag();
                //convertview의 태그를 가져와 holder에 초기화한다. 따라서 holder는 convertview의 태그가 된다.
            }
            holder.tvItemGridView.setText("" + getItem(position)); //그리드뷰 각 포지션에 날짜 입력
            //--------------글자 색 설정---------------
            int color = Color.BLACK;
            switch (position % 7) {
                case 0: // 일요일은 빨강색으로 설정
                    color = Color.RED;
                    break;
                case 6: // 토요일은 파랑으로 설정
                    color = Color.BLUE;
                    break;
            }
            holder.tvItemGridView.setTextColor(color);
            //그리드뷰에 적힌 날짜 색 설정
            return convertView;
        }
    }

    private class ViewHolder {// ViewHolder 클래스(태그에 쓰일 클래스)
        TextView tvItemGridView;
    }

    private void setCalendar(int year, int month){
       //매달 첫날의 요일, 마지막날이 30일인지 31인지를 판별
        Calendar calendar = Calendar.getInstance(); //calendar 객체
        //입력받은 각각의 연도 달을 설정
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);// firstday를 만들기 위해 입력받은 년, 월에 대한 일자를 1로 설정한다.
        firstday = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        //첫날을 설정하는데 Calendar.DAY_OF_WEEK는 일요일부터 차례로 1, 2, 3...으로 설정이 된다
        //convertview, daylist는 모두 0부터 시작하니 Calendar.DAY_OF_WEEK과 맞추기 위해 -1을 해준다
        lastday = calendar.getActualMaximum(Calendar.DATE);
        // 마지막날은 해당 달의 최대값
        for (int i=0; i<6*7; i++) { // 7행6열의 리스트를 만든다
            if ( i < firstday || i > (lastday + firstday - 1)) daylist.add("");
            // 첫날보다 작거나, 마지막날보다 크면 공백으로해줌
            else //그렇지 않으면 첫날부터 마지막날까지 써줌
                daylist.add("" + (i - firstday + 1));
        }
    }
}
