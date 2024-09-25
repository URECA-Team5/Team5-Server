package ureca.team5.handicine.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ureca.team5.handicine.entity.Medicine;
import ureca.team5.handicine.repository.MedicineRepository;

import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;
    @Value("${api.key}")
    private String apiKey;

    @Autowired
    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public List<Medicine> getMedicineInfo(String itemName) {
        List<Medicine> medicines = medicineRepository.findByItemNameContaining(itemName);
        if (medicines.isEmpty()) {
        System.out.println("제품을 찾을수 없다!");
            // DB에서 약품을 찾을 수 없을 때 외부 API 호출
            String response = fetchMedicineDataFromApi(itemName);
            // JSON 파싱 및 DB에 저장
            saveMedicineData(response);
        }
        return medicineRepository.findByItemNameContaining(itemName); // DB에서 다시 조회
    }

    private String fetchMedicineDataFromApi(String itemName) {
        // 외부 API 요청 로직 (이전에 작성한 코드를 여기에 넣기)
    	try {
            // URL 인코딩
            String encodedItemName = URLEncoder.encode(itemName, StandardCharsets.UTF_8.toString());

            // URL 문자열 생성
            String urlString = "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList?"
                    + "serviceKey=" + apiKey
                    + "&itemName=" + encodedItemName
                    + "&type=json";

            // URL 객체 생성
            URL url = new URL(urlString);
            System.out.println("아이템 이름: " + itemName);
            System.out.println("요청 URL: " + url);

            // HTTP 연결 열기
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 응답 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 연결 종료
            connection.disconnect();
            System.out.println(response.toString());

            // JSON 파싱
            JSONObject jsonResponse = new JSONObject(response.toString());

            JSONObject body = jsonResponse.getJSONObject("body");
            JSONArray items = body.getJSONArray("items");

            // 파싱한 데이터를 반복문을 통해 콘솔창에 출력
            StringBuilder entpNames = new StringBuilder();
            System.out.println("제품 개수 : "+items.length());
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                if (item.has("itemName")) {
                    entpNames.append(item.getString("efcyQesitm")).append("\n");
                }
                
                System.out.println("업체명 : "+item.getString("entpName"));
                System.out.println("제품명 : "+item.getString("itemName"));
                	System.out.println("효능 : "+item.getString("efcyQesitm"));
                System.out.println("사용법 : "+item.getString("useMethodQesitm"));
                System.out.println("주의사항 : "+item.getString("atpnQesitm"));
                if(item.isNull("intrcQesitm")) {
                	System.out.println("상호작용 : 없음");
                }
                else {
                	System.out.println("상호작용 : "+item.getString("intrcQesitm"));
                }
                if(item.isNull("seQesitm")) {
                	System.out.println("부작용 : 없음");
                }
                else {
                	System.out.println("부작용 : "+item.getString("seQesitm"));
                }
                
                
                System.out.println("보관법 : "+item.getString("depositMethodQesitm"));
            }
            return response.toString();
        } catch (IOException e) {
            // 예외 처리
            e.printStackTrace();
            return "Error occurred while fetching data.";
        }
        
    }

    private void saveMedicineData(String jsonResponse) {
        // JSON 파싱 및 데이터베이스에 저장하는 로직
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONObject body = jsonObject.getJSONObject("body");
        JSONArray items = body.getJSONArray("items");
        System.out.println("제품 개수 : "+items.length());
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            Medicine medicine = new Medicine();

            medicine.setItemName(item.getString("itemName"));
            medicine.setEntpName(item.getString("entpName"));
            medicine.setEfcyQesitm(item.getString("efcyQesitm"));
            medicine.setUseMethodQesitm(item.getString("useMethodQesitm"));
            medicine.setAtpnQesitm(item.getString("atpnQesitm"));
            medicine.setIntrcQesitm(item.optString("intrcQesitm", "없음")); // null 처리
            medicine.setSeQesitm(item.optString("seQesitm", "없음")); // null 처리
            medicine.setDepositMethodQesitm(item.getString("depositMethodQesitm"));
            medicine.setItemImage(item.optString("itemImage","없음"));
            medicineRepository.save(medicine); // DB에 저장
        }
    }
}
