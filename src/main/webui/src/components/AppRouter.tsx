import React from 'react';
import {
    BrowserRouter as Router,
    Route,
    Routes
} from "react-router-dom";
import Overview from "../views/Overview";

const AppRouter = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Overview/>}/>
            </Routes>
        </Router>
    );
}

export default AppRouter;