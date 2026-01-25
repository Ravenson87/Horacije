package com.example.Horacije.administration.services;

import com.example.Horacije.administration.model.Bill;
import com.example.Horacije.administration.repository.BillRepository;
import com.example.Horacije.administration.sharedTools.exceptions.CustomException;
import com.example.Horacije.administration.sharedTools.helpers.Helpers;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BillServiceCrud {

    private final BillRepository billRepository;

    /**
     * Saves a list of Bill entities in a single batch operation.
     *
     * @param models list of Bill entities to persist
     * @return HTTP response indicating the result of the operation
     * @throws CustomException if list is null or empty, or if saving fails
     */
    public ResponseEntity<String> createBill(List<Bill> models) {

    if(models == null || models.isEmpty()) {
        throw new CustomException("Bill list is empty or null");
    }

     try {
         billRepository.saveAll(models);
         return ResponseEntity.ok().body("Bills saved successfully");
     }catch (Exception e){
         throw new CustomException("Failed to save bills", e);
     }
    }

    /**
     * Retrieves all Bill entities and returns them as an HTTP response.
     *
     * @return 200 OK with the list of bills, or 204 No Content if none exist
     */
    public ResponseEntity<List<Bill>> readAllBills() {
        List<Bill> result = Helpers.listConverter(billRepository.findAll());
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok().body(result);
    }

    /**
     * Retrieves a Bill entity by its ID and returns it as an HTTP response.
     *
     * @param id identifier of the Bill to retrieve
     * @return 200 OK with the Bill if found, or 204 No Content if not present
     */
    public ResponseEntity<Bill> readBillById(Integer id) {
        Optional<Bill> result = billRepository.findById(id);
        //Namerno koristen ovaj map(), kako bih ga naucio, iako moze i bez njega, sa if-else
        return result
                .map(response -> ResponseEntity.ok().body(response))
                .orElse(ResponseEntity.noContent().build());
//        /**
//         * Razmisli i o ovoj verziji, koja, istini na volju, deluje mnogo jednostavnije
//         *
//         * public ResponseEntity<Bill> readBillById(Integer id) {
//         *     Bill bill = billRepository.findById(id)
//         *             .orElseThrow(() -> new CustomException("Bill not found"));
//         *
//         *     return ResponseEntity.ok(bill);
//         */

    }

    /**
     * Retrieves a Bill entities by its article name and returns it as an HTTP response
     *
     * @param articleName identifier of the Bill to retrive
     * @return 200 OK with the Bill if found, or 204 No Content if not present
     */
    public ResponseEntity<List<Bill>> readAllBillsByArticleName(String articleName) {
        List<Bill> bills = Helpers.listConverter(billRepository.findAllByArticleName(articleName));
        return bills.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok().body(bills);
    }

    /**
     * Retrieves a Bill entities by its article type and returns it as an HTTP response
     *
     * @param articleType identifier of the Bill to retrive
     * @return 200 OK with the Bill if found, or 204 No Content if not present
     */
    public ResponseEntity<List<Bill>> readAllBillsByArticleType(String articleType) {
        List<Bill> bills = Helpers.listConverter(billRepository.findAllByArticleType(articleType));
        return bills.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok().body(bills);
    }

    /**
     * Retrieves a single bill by its exact bill date.
     *
     * @param billDate the date of the bill to retrieve
     * @return 200 OK with the bill if found
     * @throws CustomException if no bill exists for the given date
     */
    public ResponseEntity<Bill> readByBillDate(LocalDate billDate) {
        Bill bill = billRepository.findByBillDate(billDate)
                .orElseThrow(()-> new CustomException("Bill Date is empty"));
        return ResponseEntity.ok().body(bill);
    }

    /**
     * Updates an existing Bill entity by its ID.
     *
     * @param id identifier of the Bill to update
     * @param model updated Bill data
     * @return 200 OK if the update succeeds
     * @throws CustomException if the Bill does not exist or the update fails
     */
    public ResponseEntity<String> update(Integer id, Bill model){
        if(!billRepository.existsById(id)){
            throw new CustomException("Bill with id " + id + " doesn't exist");
        }
        try {
           model.setId(id);
           billRepository.save(model);
            return ResponseEntity.ok().body("Bill updated successfully");
        }catch (Exception e){
            throw new CustomException("Failed to update bill", e);
        }
    }

    /**
     * Deletes a Bill entity by its ID.
     *
     * @param id identifier of the Bill to delete
     * @return 200 OK if the deletion succeeds
     * @throws CustomException if the Bill does not exist or deletion fails
     */
    public ResponseEntity<String> deleteBill(Integer id) {
        if(!billRepository.existsById(id)){
            throw new CustomException("Bill with id " + id + " doesn't exist");
        }
        try {
            billRepository.deleteById(id);
            return ResponseEntity.ok().body("Bill deleted successfully");
        } catch (Exception e){
            throw new CustomException("Failed to delete bill", e);
        }
    }

}
