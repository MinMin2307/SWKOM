import React, { useState } from 'react';
import axios from 'axios';

// Use environment variable with default fallback
const SPRING_URL = process.env.REACT_APP_SPRING_URL || 'http://localhost:8081';

function App() {
  const [selectedFile, setSelectedFile] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);

  const [expandedIndex, setExpandedIndex] = useState(null);

  const handleToggle = (index) => {
    setExpandedIndex(index === expandedIndex ? null : index);
  };

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
  };

  const handleUpload = () => {
    const formData = new FormData();
    formData.append('file', selectedFile);

    axios.post(`${SPRING_URL}/document/upload`, formData)
      .then(response => {
        alert(response.data);
      })
      .catch(error => {
        console.error('Error uploading document:', error);
      });
  };

  const handleSearch = () => {
    if (!searchQuery || searchQuery.trim() === '') {
      console.warn('Search query is empty');
      return;
    }

    axios.get(`${SPRING_URL}/elastic/search`, { params: { keyword: searchQuery.trim() } })
      .then(response => {
        console.log(response.data);
        if (response.data && response.data.length > 0) {
          setSearchResults(response.data);

        } else {
          console.warn('No documents found for the given query.');
          setSearchResults([]); // Clear results if no matches are found
        }
      })
      .catch(error => {
        console.error('Error searching for documents:', error.response?.data || error.message);
        alert('An error occurred while searching. Please try again.');
      });
  };


  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gray-100">
      <div className="p-6 bg-white rounded shadow-md w-full max-w-md">
        <h1 className="text-2xl font-bold mb-4">Document Management System</h1>

        <div className="mb-6">
          <label className="block text-gray-700">Upload Document</label>
          <input type="file" onChange={handleFileChange} className="mt-2 mb-4" />
          <button
            onClick={handleUpload}
            className="px-4 py-2 bg-blue-500 text-white rounded">
            Upload
          </button>
        </div>

        <div className="mb-6">
          <label className="block text-gray-700">Search Document</label>
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="mt-2 p-2 border rounded w-full"
            placeholder="Search by name or keyword"
          />
          <button
            onClick={handleSearch}
            className="mt-2 px-4 py-2 bg-green-500 text-white rounded">
            Search
          </button>
        </div>

        <div>
          <h2 className="text-lg font-bold mb-3">Search Results</h2>
          {searchResults && searchResults.length > 0 ? (
            <ul className="list-disc pl-5 space-y-3">
              {searchResults.map((document, index) => (
                <li key={index}>
                  <div
                    className="bg-gray-100 p-4 rounded-md shadow-md cursor-pointer"
                    onClick={() => handleToggle(index)}
                  >
                    <p className="text-sm text-gray-500">
                      <span className="font-semibold">ID:</span> {document.id}
                    </p>
                    <p className="text-sm text-gray-700 mt-1">
                      <span className="font-semibold">Path:</span> {document.path}
                    </p>
                    <p className="text-sm text-gray-800 mt-2">
                      <span className="font-semibold">Extracted Text:</span>{" "}
                      {expandedIndex === index
                        ? document.extractedText
                        : `${document.extractedText.substring(0, 20)}...`}
                    </p>
                  </div>
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-gray-500 italic">No results found. Try a different query.</p>
          )}
        </div>
      </div>
    </div>
  );
}

export default App;
