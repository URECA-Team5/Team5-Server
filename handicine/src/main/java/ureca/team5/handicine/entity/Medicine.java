package ureca.team5.handicine.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity()
@Data
@Table(name="Medicine")
public class Medicine {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
    private Long userId;

	@Column(name="item_name",length = 255)
    private String itemName; // 제품명
	@Column(name="entp_name",length = 255)
    private String entpName; // 업체명
	@Column(name="efcy_qesitm",length = 255)
    private String efcyQesitm; // 효능
	@Column(name="use_method_qesitm",length = 255)
    private String useMethodQesitm; // 사용법
	@Column(name="atpn_qesitm",length = 1000)
    private String atpnQesitm; // 주의사항
	@Column(name="intrc_qesitm",length = 255)
    private String intrcQesitm; // 상호작용
	@Column(name="se_qesitm",length = 1000)
    private String seQesitm; // 부작용
	@Column(name="deposit_method_qesitm",length = 255)
    private String depositMethodQesitm; // 보관법
	@Column(name="item_image",length= 255)
	private String itemImage;
    
   
    
}
