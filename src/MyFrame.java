import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.TextField;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import javax.security.auth.callback.LanguageCallback;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.baidu.translate.demo.TransApi;
import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechUtility;

public class MyFrame extends JFrame {
  protected static String BAIDU_APP_ID = "";
  protected static String BAIDU_SECURITY_KEY = "";
  protected static String IFLY_APP_ID = "";
  private static final long serialVersionUID = 1L;
  JPanel panelNorth, panelSouth, panelCenter;
  JTextArea textArea;
  JLabel textField;
  JButton button_start, button_stop, button_translate, button_finish;
  private SpeechRecognizer mIat;
  private String lastString = "";
  private boolean done = false;
  private static String language = "zh_cn";// "zh_cn" or "en_us"

  public static void main(String[] args) {
    new MyFrame();

  }

  public static void setLanguage(String a) {
    language = a;
  }

  public void editText(String text) {
    textField.setText(toHTMLString(text));
  }

  public void editTextArea(String text) {
    textArea.setText(text);
  }

  public String getLastResult() {
    return lastString;
  }

  public void setDone(Boolean a) {
    done = a;
  }

  public Boolean getFinished() {
    return done;
  }


  public MyFrame() {
    initIfly();
    Container con = this.getContentPane();
    con.setLayout(new BorderLayout());
    this.setSize(500, 600);
    this.setLocationRelativeTo(null);
    this.setResizable(true);
    this.setDefaultCloseOperation(3);
    this.setLayout(new BorderLayout());
    setFrame();
    this.add(panelSouth, BorderLayout.SOUTH);
    this.add(panelNorth, BorderLayout.NORTH);
    this.add(panelCenter, BorderLayout.CENTER);
  }

  public void initIfly() {
    mIat = SpeechRecognizer.createRecognizer();
    SpeechUtility.createUtility("appid=" + IFLY_APP_ID);
  }

  public void setFrame() {
    StringBuilder inArea = new StringBuilder();
    textField = new JLabel("DEFALUT", SwingConstants.LEFT);
    textField.setLocation(10, 1);
    panelNorth = new JPanel();
    panelSouth = new JPanel();
    panelCenter = new JPanel();
    textArea = new JTextArea(10, 30);
    textArea.setEditable(true);
    textArea.setLineWrap(true);
    textArea.setText("");
    button_finish = new JButton("Finish");
    button_finish.addActionListener(e -> {
      lastString = textArea.getText();
      textArea.setText("");
      inArea.delete(0, inArea.length());
      this.setVisible(false);
      done = true;
    });
    if (language.equals("zh_cn")) {
      button_translate = new JButton("Translate to English");
      button_translate.addActionListener(e -> {
        button_translate.setEnabled(false);
        TransApi api = new TransApi(BAIDU_APP_ID, BAIDU_SECURITY_KEY);
        try {
          textArea.setText(JsonParser
              .parseTranslateResult(api.getTransResult(textArea.getText(), "auto", "en")));
        } catch (UnsupportedEncodingException e1) {
          e1.printStackTrace();
        }
        inArea.delete(0, inArea.length());
        inArea.append(textArea.getText());
        button_translate.setEnabled(true);
      });
    }
    button_start = new JButton("Start listen");
    button_start.addActionListener(e -> {
      setting();
      inArea.delete(0, inArea.length());
      inArea.append(textArea.getText());
      if (!mIat.isListening())
        mIat.startListening(new RecognizerListener() {
          private ArrayList<String> toAdd = new ArrayList<String>();

          @Override
          public void onBeginOfSpeech() {
            button_start.setText("Listening...");
            button_start.setEnabled(false);
          }

          @Override
          public void onEndOfSpeech() {}

          /**
           * 获取听写结果. 获取RecognizerResult类型的识别结果，并对结果进行累加，显示到Area里
           */
          @Override
          public void onResult(RecognizerResult results, boolean islast) {

            String text = JsonParser.parseIatResult(results.getResultString(), toAdd);
            JSONTokener tokener = new JSONTokener(results.getResultString());
            try {
              JSONObject joResult = new JSONObject(tokener);
              System.out.print(joResult.toString(2));
            } catch (JSONException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            textArea.setText(inArea.toString() + text);
            if (islast) {
              inArea.delete(0, inArea.length());
              inArea.append(textArea.getText());
              iatSpeechInitUI();
            }
          }

          @Override
          public void onVolumeChanged(int volume) {}

          @Override
          public void onError(SpeechError error) {
            if (null != error) {
              JOptionPane.showMessageDialog(null, error.getErrorDescription(true),
                  "ERROR:" + error.getErrorCode(), JOptionPane.ERROR_MESSAGE);
              System.out.print((error.getErrorDescription(true)));
              inArea.delete(0, inArea.length());
              inArea.append(textArea.getText());
              iatSpeechInitUI();
            }
          }

          @Override
          public void onEvent(int eventType, int arg1, int agr2, String msg) {}
        });
      else
        mIat.stopListening();
    });
    button_stop = new JButton("Stop listen");
    button_stop.addActionListener(e -> {
      mIat.stopListening();
      iatSpeechInitUI();
    });
    panelNorth.add(textField);
    panelCenter.add(textArea);
    panelCenter.add(new JScrollPane(textArea));
    panelSouth.add(button_start);
    panelSouth.add(button_stop);
    if (language.equals("zh_cn"))
      panelSouth.add(button_translate);
    panelSouth.add(button_finish);
  }

  void setting()// 属性设置
  {
    final String engType = "cloud";
    mIat.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
    mIat.setParameter(SpeechConstant.SAMPLE_RATE, "16000");//
    mIat.setParameter(SpeechConstant.NET_TIMEOUT, "20000");
    mIat.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "60000");
    mIat.setParameter(SpeechConstant.LANGUAGE, language);// 语言en_us(英语)
    // zh_cn(中文)

    mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
    mIat.setParameter("dwa", "wpgs");
    mIat.setParameter(SpeechConstant.DOMAIN, "iat");
    mIat.setParameter(SpeechConstant.VAD_BOS, "5000");
    mIat.setParameter(SpeechConstant.VAD_EOS, "1800");
    mIat.setParameter(SpeechConstant.ASR_NBEST, "1");
    mIat.setParameter(SpeechConstant.ASR_WBEST, "1");
    mIat.setParameter(SpeechConstant.ASR_PTT, "1");// 标点符号 0(关) 1(开)
    mIat.setParameter(SpeechConstant.RESULT_TYPE, "json"); // 返回数据格式
                                                           // plain或者jason
    mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, null);
  }

  // private RecognizerListener recognizerListener1 = new RecognizerListener() { };

  public void iatSpeechInitUI() {
    button_start.setEnabled(true);
    button_start.setText("Start listen");
  }


  public String toHTMLString(String in) {
    StringBuffer out = new StringBuffer();
    int counter = 0;
    out.append("<HTML>");
    for (int i = 0; in != null && i < in.length(); i++) {
      counter++;
      char c = in.charAt(i);
      if (c == '\'')
        out.append("'");
      else if (c == '\"')
        out.append("\"");
      else if (c == '<')
        out.append("<");
      else if (c == '>')
        out.append(">");
      else if (c == '&')
        out.append("&");
      else if (c == ' ')
        out.append(" ");
      else if ((in.length() - i > 1) && c == '\r' && in.charAt(i + 1) == '\n') {
        i += 1;
        out.append("<br>");
        counter = 0;
      } else if (c == '\n') {
        out.append("<br>");
        counter = 0;
      } else
        out.append(c);
      if (counter == 60) {
        counter = 0;
        out.append("<br>");
      }
    }
    out.append("</HTML>");
    return out.toString();

  }

}
