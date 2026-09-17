import Sidebar from "../components/Sidebar"
import Header from "../components/Header"

import "./ClientPanel.css";

export default function ClientPanel () {
    return (
        <div className="template-wrapper">
            <Sidebar />
            <Header />

            <main className="main-content">
                <p>Text</p>
            </main>
        </div>
    )
}