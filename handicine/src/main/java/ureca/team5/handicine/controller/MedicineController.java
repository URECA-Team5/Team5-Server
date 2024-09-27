package ureca.team5.handicine.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ureca.team5.handicine.entity.Medicine;
import ureca.team5.handicine.service.MedicineService;
//import org.springframework.web.bind.annotation.RestController;
//PharmacyController.java
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/medicines")
public class MedicineController {

 private final MedicineService medicineService;

 @Autowired
 public MedicineController(MedicineService medicineService) {
     this.medicineService = medicineService;
 }



 @GetMapping("/search")
 public List<Medicine> search(@RequestParam("itemName") String itemName) {
//     String info = pharmacyService.getPharmacyInfo(itemName);
	 System.out.println("search : 요기 들어옴");
	return medicineService.getMedicineInfo(itemName);
     
    
     
 }
}


