import {useNavigate} from "react-router-dom";
import React, {useState} from "react";
import "./AddBillPage.css";


export default function AddBillPage() {
    const [articleName, setArticleName] = useState("");
    const [articlePrice, setArticlePrice] = useState("");
    const [articleType, setArticleType] = useState("");
    const today = new Date().toISOString().split("T")[0];
    const [billDate, setBillDate] = useState(today);
    const [items, setItems] = useState([]);
    const totalSum = items.reduce((sum, item) => sum + item.articlePrice * item.quantity, 0);
    const removeItem = (index) => {
        const updated = items.filter((_, i) => i !== index);
        setItems(updated);
    };
    const increaseQuantity = (index) => {
        const updated = [...items];
        updated[index].quantity += 1;
        setItems(updated);
    };

    const decreaseQuantity = (index) => {
        const updated = [...items];
        if (updated[index].quantity > 1) {
            updated[index].quantity -= 1;
            setItems(updated);
        }
    };

    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [brandName, setBrandName] = useState("");
    const [toasts, setToasts] = useState([]);
    const showToast = (message, type) => {
        const id = Date.now();
        setToasts([...toasts, {id, message, type}]);

        setTimeout(() => {
            setToasts((current) => current.filter((t) => t.id !== id));
        }, 3000);
    };


    const addItem = () => {
        setErrorMessage("");
        setSuccessMessage("");

        if (!articleName.trim()) {
            showToast("Naziv artikla je obavezan", "error");
            return;
        }

        if (!articlePrice || isNaN(articlePrice) || Number(articlePrice) <= 0) {
            showToast("Cena mora biti broj veći od 0", "error");
            return;
        }

        if (!billDate) {
            showToast("Datum je obavezan", "error");
            return;
        }

        const newItem = {
            articleName,
            articlePrice: Number(articlePrice),
            articleType,
            brandName: brandName || null,
            billDate,
            quantity: 1
        };


        setItems([...items, newItem]);

        showToast("Artikal dodat!", "success");

        // reset polja
        setArticleName("");
        setArticlePrice("");
        setArticleType("");
        setBrandName("");
        setBillDate(today);
    };


    const saveAll = async () => {
        setErrorMessage("");
        setSuccessMessage("");

        // Ako nema NIŠTA popunjeno i nema artikala u listi → nema šta da se šalje
        if (
            items.length === 0 &&
            !articleName &&
            !articlePrice &&
            !articleType &&
            !brandName &&
            !billDate
        ) {
            showToast("Nema artikala za slanje", "error");
            return;
        }


        let payload = [];

        items.forEach(item => {
            for (let i = 0; i < item.quantity; i++) {
                payload.push({
                    bill_date: item.billDate,
                    article_name: item.articleName,
                    article_price: item.articlePrice,
                    article_type: item.articleType || null,
                    brand_name: item.brandName || null
                });
            }
        });


        //  Dodaj trenutni artikal SAMO ako je validan
        if (articleName.trim() && articlePrice && Number(articlePrice) > 0) {
            payload.push({
                bill_date: billDate,
                article_name: articleName,
                article_price: Number(articlePrice),
                article_type: articleType || null,
                brand_name: brandName || null
            });
        }


        try {
            setLoading(true);

            const response = await fetch("http://localhost:8080/api/v1/bill/create", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const errorText = await response.text();
                setErrorMessage("Greška sa servera: " + errorText);
                return;
            }

            const result = await response.text();
            showToast("Uspešno sačuvano!", "success");


            setItems([]);
            setArticleName("");
            setArticlePrice("");
            setArticleType("");
            setBrandName("");
            setBillDate(today);
        } catch (error) {
            setErrorMessage("Došlo je do greške: " + error.message);
        } finally {
            setLoading(false);
        }
    };


    return (
        <div className="addbill-container">
            <div className="toast-container">
                {toasts.map((toast) => (
                    <div key={toast.id} className={`toast ${toast.type}`}>
                        {toast.message}
                    </div>
                ))}
            </div>

            <div className="addbill-card">
                <h2>Unos artikala</h2>

                <div className="form-group">
                    <label>Naziv artikla *</label>
                    <input
                        type="text"
                        value={articleName}
                        onChange={(e) => setArticleName(e.target.value)}
                    />
                </div>

                <div className="form-group">
                    <label>Cena artikla *</label>
                    <input
                        type="number"
                        value={articlePrice}
                        onChange={(e) => setArticlePrice(e.target.value)}
                    />
                </div>

                <div className="form-group">
                    <label>Tip artikla</label>
                    <select
                        value={articleType}
                        onChange={(e) => setArticleType(e.target.value)}
                    >
                        <option value="">Izaberi...</option>
                        <option value="hrana">Hrana</option>
                        <option value="pice">Piće</option>
                        <option value="duvan">Duvan</option>
                        <option value="slatkisi">Slatkiši</option>
                        <option value="higijena">Higijena</option>
                        <option value="ostalo">Ostalo</option>
                    </select>
                </div>
                <div className="form-group">
                    <label>
                        Ime brenda <span className="optional-text">(opciono)</span>
                    </label>
                    <input
                        type="text"
                        value={brandName}
                        onChange={(e) => setBrandName(e.target.value)}
                        placeholder="Unesi ime brenda"
                    />
                </div>


                <div className="form-group">
                    <label>Datum kupovine</label>
                    <input
                        type="date"
                        value={billDate}
                        onChange={(e) => setBillDate(e.target.value)}
                    />
                </div>

                <div className="button-row">
                    <button className="secondary-btn" onClick={addItem}>
                        Sledeći artikal
                    </button>

                    <button className="primary-btn" onClick={saveAll}>
                        Sačuvaj sve
                    </button>
                </div>
            </div>
            <button className="back-btn" onClick={() => navigate("/")}>
                ← Nazad na početnu
            </button>

            {items.length > 0 && (
                <div className="addbill-list">
                    {loading && <div className="loading-spinner"></div>}

                    {successMessage && (
                        <div className="success-popup">
                            {successMessage}
                        </div>
                    )}

                    {errorMessage && (
                        <div className="error-card">
                            {errorMessage}
                        </div>
                    )}

                    <h3>Artikli spremni za čuvanje:</h3>
                    <p className="total-sum">Ukupno: {totalSum} RSD</p>

                    <ul>
                        {items.map((item, index) => (
                            <li key={index} className="list-item">
                                <div className="item-info">
                                    {item.articleName} — {item.articlePrice} RSD × {item.quantity}
                                </div>

                                <div className="quantity-controls">
                                    <button className="qty-btn" onClick={() => decreaseQuantity(index)}>−</button>
                                    <button className="qty-btn" onClick={() => increaseQuantity(index)}>+</button>
                                </div>

                                <button className="delete-btn" onClick={() => removeItem(index)}>Obriši</button>
                            </li>


                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
}
