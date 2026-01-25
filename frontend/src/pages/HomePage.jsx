import React from "react";
import { useNavigate } from "react-router-dom";
import "./HomePage.css";

function HomePage() {
    const navigate = useNavigate();

    return (
        <div className="home-container">
            <div className="home-content">
                <h1>Dobrodošli u Horacije</h1>

                <div className="home-buttons">
                    <button onClick={() => navigate("/add-bill")}>
                        Unesi artikal
                    </button>

                    <button onClick={() => navigate("/check-purchases")}>
                        Proveri kupovine
                    </button>
                </div>
            </div>
        </div>
    );
}

export default HomePage;
