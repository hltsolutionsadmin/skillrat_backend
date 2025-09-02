package com.skillrat.usermanagement.repository;

import com.skillrat.usermanagement.model.AddressModel;
import com.skillrat.usermanagement.model.B2BUnitModel;
import com.skillrat.usermanagement.model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface B2BUnitRepository extends JpaRepository<B2BUnitModel, Long> {

    // 1. Find by admin and business name (ignore case)
    Optional<B2BUnitModel> findByAdminAndBusinessNameIgnoreCase(UserModel admin, String businessName);

    // 2. Find by category name ordered by created date
    Page<B2BUnitModel> findByCategory_NameOrderByCreatedDateDesc(String categoryName, Pageable pageable);

    // 3. Find by admin
    Optional<B2BUnitModel> findByAdmin(UserModel admin);

    // 4. Find by admin ID
    @Query("SELECT b FROM B2BUnitModel b WHERE b.admin.id = :adminId")
    List<B2BUnitModel> findByAdminId(@Param("adminId") Long adminId);

    // 5. Find nearby businesses with optional category filter (native query with pagination)
    @Query(value = """
            SELECT * FROM (
                SELECT bu.*, 
                       (6371 * acos(
                           cos(radians(:latitude)) * cos(radians(bu.business_latitude)) *
                           cos(radians(bu.business_longitude) - radians(:longitude)) +
                           sin(radians(:latitude)) * sin(radians(bu.business_latitude))
                       )) AS distance
                FROM b2b_unit bu
                JOIN business_categories bc ON bu.category_id = bc.id
                WHERE bu.enabled = true
                  AND (:categoryName IS NULL OR LOWER(bc.name) = LOWER(:categoryName))
            ) AS calculated
            WHERE distance <= :radiusInKm
            ORDER BY distance ASC
            """,
            countQuery = """
                    SELECT COUNT(*) FROM (
                        SELECT bu.id,
                               (6371 * acos(
                                   cos(radians(:latitude)) * cos(radians(bu.business_latitude)) *
                                   cos(radians(bu.business_longitude) - radians(:longitude)) +
                                   sin(radians(:latitude)) * sin(radians(bu.business_latitude))
                               )) AS distance
                        FROM b2b_unit bu
                        JOIN business_categories bc ON bu.category_id = bc.id
                        WHERE bu.enabled = true
                          AND (:categoryName IS NULL OR LOWER(bc.name) = LOWER(:categoryName))
                    ) AS calculated
                    """,
            nativeQuery = true)
    Page<B2BUnitModel> findNearbyBusinessesWithCategoryFilter(@Param("latitude") double latitude,
                                                              @Param("longitude") double longitude,
                                                              @Param("radiusInKm") double radiusInKm,
                                                              @Param("categoryName") String categoryName,
                                                              Pageable pageable);

    // 6. Find by admin's address postal code
    @Query("""
        SELECT DISTINCT b
        FROM B2BUnitModel b
        JOIN b.admin u
        JOIN u.addresses a
        WHERE a.postalCode = :postalCode
    """)
    Page<B2BUnitModel> findByAdminAddressPostalCode(@Param("postalCode") String postalCode, Pageable pageable);

    // 7. Find by city and category name
    @Query("""
       SELECT b FROM B2BUnitModel b
       WHERE b.enabled = true
         AND LOWER(b.businessAddress.city) = LOWER(:city)
         AND LOWER(b.category.name) = LOWER(:categoryName)
    """)
    Page<B2BUnitModel> findByCityAndCategoryName(@Param("city") String city,
                                                 @Param("categoryName") String categoryName,
                                                 Pageable pageable);

    // 8. Get business address by unit ID
    @Query("SELECT b.businessAddress FROM B2BUnitModel b WHERE b.id = :unitId")
    Optional<AddressModel> findBusinessAddressByUnitId(@Param("unitId") Long unitId);

    // 9. Check business code uniqueness
    boolean existsByBusinessCode(String businessCode);

    Optional<B2BUnitModel> findByBusinessCode(String businessCode);

    Page<B2BUnitModel> findByEnabledFalse(Pageable pageable);

}
