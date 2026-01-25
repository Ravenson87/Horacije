import React, {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import "./CheckPurchasesPage.css";

// Debounce hook
function useDebounce(value, delay) {
    const [debouncedValue, setDebouncedValue] = useState(value);

    useEffect(() => {
        const handler = setTimeout(() => {
            setDebouncedValue(value);
        }, delay);

        return () => {
            clearTimeout(handler);
        };
    }, [value, delay]);

    return debouncedValue;
}


const API_BASE_URL = "http://localhost:8080/api/v1/bill";


export default function CheckPurchasesPage() {
    const navigate = useNavigate();

    // Lista računa za trenutnu stranicu (content iz Page<Bill>)
    const [bills, setBills] = useState([]);

    // Backend paginacija
    const [currentPage, setCurrentPage] = useState(1); // 1-based za UI
    const [totalPages, setTotalPages] = useState(1);
    const itemsPerPage = 10;

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    // Sortiranje po ceni – sada ide preko backend-a (sortBy=articlePrice)
    const [sortPriceAsc, setSortPriceAsc] = useState(true);
    // Sortiranje po datumu
    const [sortDateAsc, setSortDateAsc] = useState(false);


    const [editingBill, setEditingBill] = useState(null);
    const [deleteTarget, setDeleteTarget] = useState(null);

    const [toasts, setToasts] = useState([]);

    const showToast = (message, type) => {
        const id = Date.now();
        setToasts((prev) => [...prev, {id, message, type}]);
        setTimeout(() => {
            setToasts((current) => current.filter((t) => t.id !== id));
        }, 3000);
    };

    // Filteri – UI ostaje isti, ali sada šaljemo filtere backendu
    const [filters, setFilters] = useState({
        name: "",
        type: "",
        brand: "",
        priceMin: "",
        priceMax: "",
        dateFrom: "",
        dateTo: ""
    });

    const debouncedFilters = useDebounce(filters, 1000);

    // Ukupna suma – sada je vraća backend
    const [calculatedTotal, setCalculatedTotal] = useState(null);

    // Kad se promene filteri, resetujemo sumu
    useEffect(() => {
        setCalculatedTotal(null);
        // Kada promeniš filtere, vraćamo se na prvu stranicu
        setCurrentPage(1);
    }, [filters]);

    // Funkcija za pozivanje backend pretrage
    const fetchBills = async () => {
        try {
            setLoading(true);
            setError("");

            const params = new URLSearchParams();

            // Filteri – šaljemo samo ako nisu prazni
            if (filters.name.trim() !== "") params.append("name", filters.name.trim());
            if (filters.type.trim() !== "") params.append("type", filters.type.trim());
            if (filters.brand.trim() !== "") params.append("brand", filters.brand.trim());
            if (filters.priceMin !== "") params.append("priceMin", filters.priceMin);
            if (filters.priceMax !== "") params.append("priceMax", filters.priceMax);
            if (filters.dateFrom !== "") params.append("dateFrom", filters.dateFrom);
            if (filters.dateTo !== "") params.append("dateTo", filters.dateTo);

            // Paginacija – backend je 0-based, frontend 1-based
            params.append("page", currentPage - 1);
            params.append("size", itemsPerPage);

            if (sortDateAsc !== null) {
                params.append("sortBy", "billDate");
                params.append("direction", sortDateAsc ? "asc" : "desc");
            } else {
                params.append("sortBy", "articlePrice");
                params.append("direction", sortPriceAsc ? "asc" : "desc");
            }


            const response = await fetch(`${API_BASE_URL}/search?${params.toString()}`);

            if (!response.ok) {
                throw new Error("Greška pri učitavanju podataka");
            }

            const data = await response.json();
            // Očekujemo Page<Bill> JSON:
            // {
            //   content: [...],
            //   totalPages: number,
            //   number: currentPage (0-based),
            //   ...
            // }

            setBills(data.content || []);
            setTotalPages(data.totalPages || 1);

        } catch (err) {
            setError(err.message || "Došlo je do greške");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBills();
    }, [currentPage, sortPriceAsc,sortDateAsc, debouncedFilters]);

    // Poziv backend-a za sumu
    const fetchSum = async () => {
        try {
            setLoading(true);
            setError("");

            const params = new URLSearchParams();

            if (debouncedFilters.name.trim() !== "") params.append("name", debouncedFilters.name.trim());
            if (debouncedFilters.type.trim() !== "") params.append("type", debouncedFilters.type.trim());
            if (debouncedFilters.brand.trim() !== "") params.append("brand", debouncedFilters.brand.trim());
            if (debouncedFilters.priceMin !== "") params.append("priceMin", debouncedFilters.priceMin);
            if (debouncedFilters.priceMax !== "") params.append("priceMax", debouncedFilters.priceMax);
            if (debouncedFilters.dateFrom !== "") params.append("dateFrom", debouncedFilters.dateFrom);
            if (debouncedFilters.dateTo !== "") params.append("dateTo", debouncedFilters.dateTo);


            const response = await fetch(`${API_BASE_URL}/sum?${params.toString()}`);

            if (response.status === 204) {
                // Nema rezultata – možeš da prikažeš 0 ili crtu
                setCalculatedTotal(0);
                return;
            }

            if (!response.ok) {
                throw new Error("Greška pri izračunavanju sume");
            }

            const data = await response.json();
            // Pretpostavljamo da backend vraća broj (BigDecimal kao JSON broj)
            setCalculatedTotal(data);

        } catch (err) {
            setError(err.message || "Došlo je do greške pri sabiranju");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="check-container">

            {/* Toastovi */}
            <div className="toast-container">
                {toasts.map((toast) => (
                    <div key={toast.id} className={`toast ${toast.type}`}>
                        {toast.message}
                    </div>
                ))}
            </div>

            <h2 className="title">Pregled kupovina</h2>

            {/* FIXED BACK BUTTON */}
            <button className="back-btn" onClick={() => navigate("/")}>
                ← Nazad na početnu
            </button>

            {/* Sort by price – sada menja backend direction */}
            <button
                className="sort-btn"
                onClick={() => setSortPriceAsc((prev) => !prev)}
            >
                {sortPriceAsc
                    ? "Sortiraj cenu: niža → viša"
                    : "Sortiraj cenu: viša → niža"}
            </button>

            {/* Sort by date */}
            <button
                className="sort-btn"
                onClick={() => setSortDateAsc((prev) => !prev)}
            >
                {sortDateAsc
                    ? "Sortiraj datum: stariji → noviji"
                    : "Sortiraj datum: noviji → stariji"}
            </button>


            {/* FILTER BAR */}
            <div className="filter-bar">

                {/* Naziv */}
                <div className="filter-item">
                    <input
                        type="text"
                        placeholder="Naziv"
                        value={filters.name}
                        onChange={(e) => {
                            const value = e.target.value;
                            setFilters({ ...filters, name: value, type: value ? "" : filters.type });
                        }}
                    />
                    {filters.name && (
                        <button className="clear-btn" onClick={() => setFilters({ ...filters, name: "" })}>✕</button>
                    )}
                </div>

                {/* Tip */}
                <div className="filter-item">
                    <select
                        value={filters.type}
                        onChange={(e) => setFilters({ ...filters, type: e.target.value })}
                    >
                        <option value="">Tip</option>
                        <option value="hrana">Hrana</option>
                        <option value="piće">Piće</option>
                        <option value="duvan">Duvan</option>
                        <option value="slatkiši">Slatkiši</option>
                        <option value="higijena">Higijena</option>
                        <option value="ostalo">Ostalo</option>
                    </select>
                    {filters.type && (
                        <button className="clear-btn" onClick={() => setFilters({ ...filters, type: "" })}>✕</button>
                    )}
                </div>

                {/* Brend */}
                <div className="filter-item">
                    <input
                        type="text"
                        placeholder="Brend"
                        value={filters.brand}
                        onChange={(e) => setFilters({ ...filters, brand: e.target.value })}
                    />
                    {filters.brand && (
                        <button className="clear-btn" onClick={() => setFilters({ ...filters, brand: "" })}>✕</button>
                    )}
                </div>

                {/* Cena od / Cena do zajedno */}
                <div className="filter-item price-range">
                    <input
                        type="number"
                        placeholder="Cena od"
                        value={filters.priceMin}
                        onChange={(e) => setFilters({ ...filters, priceMin: e.target.value })}
                    />
                    {filters.priceMin && (
                        <button className="clear-btn" onClick={() => setFilters({ ...filters, priceMin: "" })}>✕</button>
                    )}

                    <input
                        type="number"
                        placeholder="Cena do"
                        value={filters.priceMax}
                        onChange={(e) => setFilters({ ...filters, priceMax: e.target.value })}
                    />
                    {filters.priceMax && (
                        <button className="clear-btn" onClick={() => setFilters({ ...filters, priceMax: "" })}>✕</button>
                    )}
                </div>

                {/* Datum od */}
                <div className="filter-item date-filter">
                    <input
                        type="date"
                        value={filters.dateFrom}
                        onChange={(e) => setFilters({ ...filters, dateFrom: e.target.value })}
                    />
                    {filters.dateFrom && (
                        <button className="clear-btn" onClick={() => setFilters({ ...filters, dateFrom: "" })}>✕</button>
                    )}
                </div>

                {/* Datum do */}
                <div className="filter-item date-filter">
                    <input
                        type="date"
                        value={filters.dateTo}
                        onChange={(e) => setFilters({ ...filters, dateTo: e.target.value })}
                    />
                    {filters.dateTo && (
                        <button className="clear-btn" onClick={() => setFilters({ ...filters, dateTo: "" })}>✕</button>
                    )}
                </div>


                {/* Reset svih filtera */}
                <button
                    className="reset-btn"
                    onClick={() =>
                        setFilters({
                            name: "",
                            type: "",
                            brand: "",
                            priceMin: "",
                            priceMax: "",
                            dateFrom: "",
                            dateTo: ""
                        })
                    }
                >
                    Reset
                </button>
            </div>


            {/* TOTAL SUM BOX */}
            <div className="total-box">
                <h3>Potrošen novac</h3>

                {calculatedTotal !== null ? (
                    <p>{calculatedTotal} RSD</p>
                ) : (
                    <p>—</p>
                )}

                <button
                    className="calculate-btn"
                    onClick={fetchSum}
                >
                    Izračunaj sumu
                </button>
            </div>

            {loading && <div className="loader"></div>}
            {error && <div className="error-box">{error}</div>}


            <div className="list-wrapper">
                <div className="list-header">
                    <span>Naziv</span>
                    <span>Cena</span>
                    <span>Tip</span>
                    <span>Brend</span>
                    <span>Datum</span>
                    <span>Akcija</span>
                </div>

                {bills.map((bill) => (
                    <div key={bill.id} className="list-row">
                        <span>{bill.article_name}</span>
                        <span>{bill.article_price} RSD</span>
                        <span>{bill.article_type || "—"}</span>
                        <span>{bill.brand_name || "—"}</span>
                        <span>{bill.bill_date}</span>

                        <div className="action-buttons">
                            <button
                                className="update-btn"
                                onClick={() => setEditingBill(bill)}
                            >
                                Promeni
                            </button>

                            <button
                                className="delete-btn"
                                onClick={() => setDeleteTarget(bill)}
                            >
                                Obriši
                            </button>
                        </div>

                    </div>
                ))}
            </div>


            {/* PAGINATION – sada koristi backend totalPages i currentPage */}
            <div className="pagination">
                <button
                    disabled={currentPage === 1}
                    onClick={() => setCurrentPage((prev) => prev - 1)}
                >
                    Prethodna
                </button>

                {Array.from({length: totalPages}, (_, i) => (
                    <button
                        key={i}
                        className={currentPage === i + 1 ? "active-page" : ""}
                        onClick={() => setCurrentPage(i + 1)}
                    >
                        {i + 1}
                    </button>
                ))}

                <button
                    disabled={currentPage === totalPages}
                    onClick={() => setCurrentPage((prev) => prev + 1)}
                >
                    Sledeća
                </button>
            </div>

            {/* MODALI — ostaju isti, update/delete i dalje idu na stare endpoint-e */}
            {editingBill && (
                <div className="modal-overlay">
                    <div className="modal-card">
                        <h3>Izmena artikla</h3>

                        <div className="form-group">
                            <label>Naziv artikla</label>
                            <input
                                type="text"
                                value={editingBill.article_name}
                                onChange={(e) =>
                                    setEditingBill({...editingBill, article_name: e.target.value})
                                }
                            />
                        </div>

                        <div className="form-group">
                            <label>Cena</label>
                            <input
                                type="number"
                                value={editingBill.article_price}
                                onChange={(e) =>
                                    setEditingBill({...editingBill, article_price: e.target.value})
                                }
                            />
                        </div>

                        <div className="form-group">
                            <label>Tip</label>
                            <select
                                value={editingBill.article_type || ""}
                                onChange={(e) =>
                                    setEditingBill({...editingBill, article_type: e.target.value})
                                }
                            >
                                <option value="">Izaberi tip</option>
                                <option value="hrana">Hrana</option>
                                <option value="piće">Piće</option>
                                <option value="duvan">Duvan</option>
                                <option value="slatkiši">Slatkiši</option>
                                <option value="higijena">Higijena</option>
                                <option value="ostalo">Ostalo</option>
                            </select>
                        </div>

                        <div className="form-group">
                            <label>Brend</label>
                            <input
                                type="text"
                                value={editingBill.brand_name || ""}
                                onChange={(e) =>
                                    setEditingBill({...editingBill, brand_name: e.target.value})
                                }
                            />
                        </div>

                        <div className="form-group">
                            <label>Datum</label>
                            <input
                                type="date"
                                value={editingBill.bill_date}
                                onChange={(e) =>
                                    setEditingBill({...editingBill, bill_date: e.target.value})
                                }
                            />
                        </div>

                        <div className="button-row">
                            <button
                                className="secondary-btn"
                                onClick={() => setEditingBill(null)}
                            >
                                Otkaži
                            </button>

                            <button
                                className="primary-btn"
                                onClick={async () => {
                                    if (!editingBill.article_name || editingBill.article_name.trim() === "") {
                                        showToast("Naziv artikla je obavezan!", "error");
                                        return;
                                    }

                                    if (!editingBill.article_price || editingBill.article_price <= 0) {
                                        showToast("Cena mora biti uneta i veća od 0!", "error");
                                        return;
                                    }

                                    try {
                                        const response = await fetch(
                                            `http://localhost:8080/api/v1/bill/update/${editingBill.id}`,
                                            {
                                                method: "PUT",
                                                headers: {"Content-Type": "application/json"},
                                                body: JSON.stringify(editingBill),
                                            }
                                        );

                                        if (!response.ok) {
                                            throw new Error("Greška pri ažuriranju");
                                        }

                                        // Osvježavamo listu na trenutnoj stranici
                                        const updatedBills = bills.map((b) =>
                                            b.id === editingBill.id ? editingBill : b
                                        );

                                        setBills(updatedBills);
                                        showToast("Uspešno ažurirano!", "success");
                                        setEditingBill(null);

                                    } catch (err) {
                                        showToast(err.message, "error");
                                    }
                                }}
                            >
                                Sačuvaj
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {deleteTarget && (
                <div className="modal-overlay">
                    <div className="modal-card">
                        <h3>Potvrda brisanja</h3>

                        <p className="modal-text">
                            Da li ste sigurni da želite da obrišete:<br/>
                            <strong>{deleteTarget.article_name}</strong>?
                        </p>

                        <div className="button-row">
                            <button
                                className="secondary-btn"
                                onClick={() => setDeleteTarget(null)}
                            >
                                Otkaži
                            </button>

                            <button
                                className="primary-btn"
                                onClick={async () => {
                                    try {
                                        const response = await fetch(
                                            `http://localhost:8080/api/v1/bill/delete/${deleteTarget.id}`,
                                            {method: "DELETE"}
                                        );

                                        if (!response.ok) {
                                            throw new Error("Greška pri brisanju");
                                        }

                                        setBills(bills.filter((b) => b.id !== deleteTarget.id));
                                        showToast("Artikal obrisan!", "success");
                                        setDeleteTarget(null);

                                    } catch (err) {
                                        showToast(err.message, "error");
                                    }
                                }}
                            >
                                Obriši
                            </button>
                        </div>
                    </div>
                </div>
            )}

        </div>
    );
}
