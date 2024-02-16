package com.example.androidtest;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //Win condition
    final int WinConditionLine = 2;
    //亂數最小值
    final int RandomMin = 0;
    //亂數最大值
    final int RandomMax = 25;
    //橫軸
    final int Row = 3;
    //縱軸
    final int Col = 3;
    //目前連線數
    int winLine = 0;
    //紀錄亂數資料
    List<Integer> randomArray = new ArrayList();
    //賓果區按鈕
    final Btn[][]  btnList = new Btn[Row][Col];
    //Table
    final int[] tableId = {
            R.id.table1,
            R.id.table2,
            R.id.table3
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initUI();

    }

    private void initUI()
    {
        //建立賓果區
        for(int x= 0 ; x<Row; x++)
        {
            for(int y= 0 ; y<Col; y++)
            {
                TableRow row = findViewById(this.tableId[x]);
                this.btnList[x][y] =new Btn(this);
                this.btnList[x][y].init();
                this.btnList[x][y].setCallback(()->{
                    this.checkBingoLine();
                    this.refreshUI();
                });
                row.addView(this.btnList[x][y].getInstance());
            }
        }
        //建立開始遊戲按鈕
        Button randomBtn;
        randomBtn = findViewById(R.id.randomBtn);
        randomBtn.setOnClickListener(view->
        {
            for(int x= 0 ; x<Row; x++)
            {
                for(int y= 0 ; y<Col; y++)
                {
                    this.btnList[x][y].init();
                }
            }
            this.createRandom();
            this.refreshUI();
        });
        this.refreshLineInfo();
    }

    //刷新UI
    private void refreshUI()
    {
        int BtnIndex = 0;
        for(int x= 0 ; x<Row; x++)
        {
          for(int y= 0 ; y<Col; y++)
          {
              this.btnList[x][y].setText(String.format("%d", this.randomArray.get(BtnIndex)));
              BtnIndex+=1;
          }
        }
        this.refreshLineInfo();
    }
    //更新目前的連線數量
    private void refreshLineInfo()
    {
        TextView text = findViewById(R.id.textView);
        text.setTextSize(30);
        text.setText(String.format("目前連線數：%d", this.winLine));
    }
    //建立亂數
    private void createRandom()
    {
        Set tempSet = new HashSet();
        while (tempSet.size()<(Row*Col))
        {
            tempSet.add(RandomMin + (int)(Math.random()*((RandomMax - RandomMin)+1)));
        }
        this.randomArray.clear();
        tempSet.forEach((ele)->
        {
            this.randomArray.add((Integer) ele);
        });
    }
    //檢查目前的賓果數
    private void checkBingoLine()
    {
        //連線數
        int lineNum = 0;
        //掃橫軸的連線數
        for(int x= 0 ; x<Row; x++)
        {
            int combol=  0;
            for(int y= 0 ; y<Col; y++)
            {
                if(this.btnList[x][y].getState()== Btn.State.On)
                {
                    combol+=1;
                }
            }
            if(combol==Row)
            {
                lineNum+=1;
            }
        }
        //掃縱軸的連線數
        for(int x= 0 ; x<Row; x++)
        {
            int combol=  0;
            for(int y= 0 ; y<Col; y++)
            {
                if(this.btnList[y][x].getState()== Btn.State.On)
                {
                    combol+=1;
                }
            }
            if(combol==Col)
            {
                lineNum+=1;
            }
        }
        //特殊斜線
        if(this.btnList[0][0].getState()== Btn.State.On &&
                this.btnList[1][1].getState()== Btn.State.On &&
        this.btnList[2][2].getState()== Btn.State.On)
        {
            lineNum+=1;
        }
        //特殊斜線
        if(this.btnList[0][2].getState()== Btn.State.On &&
                this.btnList[1][1].getState()== Btn.State.On &&
                this.btnList[2][0].getState()== Btn.State.On)
        {
            lineNum+=1;
        }

        //更新數量
        this.winLine = lineNum;

        if(this.winLine>=WinConditionLine)
        {
            this.showWinDialog();
        }
    }
    //通知獲勝
    private void showWinDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You Win")
                .setTitle("Congratulations");
        builder.create().show();
    }
}

//賓果按鈕 callback interface
interface onBtnClickEvent
{
    public void OnEvent();
}
//賓果按鈕
 class Btn{
    //賓果按鈕狀態
    //Off 灰色狀態
    //On 紅色狀態
    enum State { Off, On};
    //目前賓果按鈕狀態
    State state = State.Off;
    //賓果按鈕 callback
    private onBtnClickEvent clickEvent;
    //賓果按鈕實體
    private Button instance = null;
    //按鈕準備完成
    private boolean isRead = false;
    Btn(Context context)
    {
        this.instance = new Button(context);
        this.instance.setOnClickListener(view -> {
            if(isRead==false)
            {
                return;
            }

            if(this.state==State.On)
            {
                this.instance.setBackgroundColor(Color.GRAY);
                this.state = State.Off;
            }
            else
            {
                this.instance.setBackgroundColor(Color.RED);
                this.state = State.On;
            }
            if(this.clickEvent!=null)
            {
                this.clickEvent.OnEvent();
            }
        });
    }
    //取得按鈕實體
    public Button getInstance(){return  this.instance;}
    //設定按鈕 callback
    public void setCallback(onBtnClickEvent event)
    {
        this.clickEvent = event;
    }

    //取得目前按鈕狀態
    public State getState(){return this.state;}
    //初始化
    public void init()
    {
        this.state = State.Off;
        this.instance.setBackgroundColor(Color.GRAY);
        this.lock();
    }
    //設定按鈕內容
    public void setText(String text)
    {
        this.instance.setText(text);
        this.unlock();
    }
    public void lock()
    {
        this.isRead = false;
    }

    public void unlock()
    {
        this.isRead = true;
    }
}