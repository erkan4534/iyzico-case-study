package com.iyzico.challenge.integrator.data.entity;

import org.hibernate.annotations.Where;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.iyzico.challenge.integrator.util.Constant.DB_PRECISION;
import static com.iyzico.challenge.integrator.util.Constant.DB_SCALE;

@Entity
@Table(name = Product.TABLE_NAME, indexes = {
        @Index(columnList = "status", name = "idx_product___status")
})
public class Product {
    public static final String TABLE_NAME = "product";
    public static final String DESCRIPTION_COLUMN_NAME = "description";
    private long id;
    private String name;
    private long userId;
    private long stockCount;
    private Status status;
    private BigDecimal price;
    private String barcode;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = LongText.class)
    @MapKey(name = "columnName")
    @JoinColumn(name = "record_id", foreignKey = @ForeignKey(name = "none"))
    @Where(clause = "table_name = '" + TABLE_NAME + "'")
    private Map<String, LongText> longTexts = new HashMap<>();

    private User user;

    public enum Status {
        IN_STOCK, OUT_OF_STOCK, UNPUBLISHED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", length = 512, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Basic
    @Min(0)
    @Column(name = "stock_count", nullable = false)
    public long getStockCount() {
        return stockCount;
    }

    public void setStockCount(long stockCount) {
        this.stockCount = stockCount;
    }

    @Basic
    @Column(name = "status", length = 16, nullable = false)
    @Enumerated(EnumType.STRING)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Basic
    @Column(name = "price", precision = DB_PRECISION, scale = DB_SCALE, nullable = false)
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Basic
    @Column(name = "barcode", length = 32, nullable = false)
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }


    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Transient
    public LongText getDescription() {
        return longTexts.get(DESCRIPTION_COLUMN_NAME);
    }

    public void setDescription(LongText description) {
        longTexts.put(DESCRIPTION_COLUMN_NAME, description);
    }

}
